/* Copyright 2008 The Android Open Source Project
 */

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <fcntl.h>

#include <private/android_filesystem_config.h>

#include "binder.h"

#if 0
#define ALOGI(x...) fprintf(stderr, "svcmgr: " x)
#define ALOGE(x...) fprintf(stderr, "svcmgr: " x)
#else
#define LOG_TAG "ServiceManager"
#include <cutils/log.h>
#endif

/* TODO:
 * These should come from a config file or perhaps be
 * based on some namespace rules of some sort (media
 * uid can register media.*, etc)
 */
static struct {
    unsigned uid;
    const char *name;
} allowed[] = {
    { AID_MEDIA, "media.audio_flinger" },
    { AID_MEDIA, "media.log" },
    { AID_MEDIA, "media.player" },
    { AID_MEDIA, "media.camera" },
    { AID_MEDIA, "media.audio_policy" },
    { AID_DRM,   "drm.drmManager" },
    { AID_NFC,   "nfc" },
    { AID_BLUETOOTH, "bluetooth" },
    { AID_RADIO, "radio.phone" },
    { AID_RADIO, "radio.sms" },
    { AID_RADIO, "radio.phonesubinfo" },
    { AID_RADIO, "radio.simphonebook" },
/* TODO: remove after phone services are updated: */
    { AID_RADIO, "phone" },
    { AID_RADIO, "sip" },
    { AID_RADIO, "isms" },
    { AID_RADIO, "iphonesubinfo" },
    { AID_RADIO, "simphonebook" },
    { AID_MEDIA, "common_time.clock" },
    { AID_MEDIA, "common_time.config" },
    { AID_KEYSTORE, "android.security.keystore" },
};

void *svcmgr_handle;

const char *str8(uint16_t *x)
{
    static char buf[128];
    unsigned max = 127;
    char *p = buf;

    if (x) {
        while (*x && max--) {
            *p++ = *x++;
        }
    }
    *p++ = 0;
    return buf;
}

int str16eq(uint16_t *a, const char *b)
{
    while (*a && *b)
        if (*a++ != *b++) return 0;
    if (*a || *b)
        return 0;
    return 1;
}

int svc_can_register(unsigned uid, uint16_t *name)
{
    unsigned n;
    //如果用户组是root用户或者system用户，则权限够高，允许注册。
    if ((uid == 0) || (uid == AID_SYSTEM))
        return 1;

    //allowed结构数组控制那些权限达不到root和system的进程
    //如果Server进程权限不够root和system,请记住要在allowed中添加相应的项
    for (n = 0; n < sizeof(allowed) / sizeof(allowed[0]); n++)
        if ((uid == allowed[n].uid) && str16eq(name, allowed[n].name))
            return 1;

    return 0;
}

struct svcinfo 
{
    struct svcinfo *next;
    void *ptr;
    struct binder_death death;
    int allow_isolated;
    unsigned len;
    uint16_t name[0];
};

struct svcinfo *svclist = 0;

struct svcinfo *find_svc(uint16_t *s16, unsigned len)
{
    struct svcinfo *si;

    for (si = svclist; si; si = si->next) {
        if ((len == si->len) &&
            !memcmp(s16, si->name, len * sizeof(uint16_t))) {
            return si;
        }
    }
    return 0;
}

void svcinfo_death(struct binder_state *bs, void *ptr)
{
    struct svcinfo *si = ptr;
    ALOGI("service '%s' died\n", str8(si->name));
    if (si->ptr) {
        binder_release(bs, si->ptr);
        si->ptr = 0;
    }   
}

uint16_t svcmgr_id[] = { 
    'a','n','d','r','o','i','d','.','o','s','.',
    'I','S','e','r','v','i','c','e','M','a','n','a','g','e','r' 
};
  

void *do_find_service(struct binder_state *bs, uint16_t *s, unsigned len, unsigned uid)
{
    struct svcinfo *si;
    si = find_svc(s, len);

//    ALOGI("check_service('%s') ptr = %p\n", str8(s), si ? si->ptr : 0);
    if (si && si->ptr) {
        if (!si->allow_isolated) {
            // If this service doesn't allow access from isolated processes,
            // then check the uid to see if it is isolated.
            unsigned appid = uid % AID_USER;
            if (appid >= AID_ISOLATED_START && appid <= AID_ISOLATED_END) {
                return 0;
            }
        }
        return si->ptr;
    } else {
        return 0;
    }
}

int do_add_service(struct binder_state *bs,
                   uint16_t *s, unsigned len,
                   void *ptr, unsigned uid, int allow_isolated)
{
    struct svcinfo *si;
    //ALOGI("add_service('%s',%p,%s) uid=%d\n", str8(s), ptr,
    //        allow_isolated ? "allow_isolated" : "!allow_isolated", uid);

    if (!ptr || (len == 0) || (len > 127))
        return -1;
    //svc_can_register函数比较注册进程的uid和名字。
    if (!svc_can_register(uid, s)) {
        ALOGE("add_service('%s',%p) uid=%d - PERMISSION DENIED\n",
             str8(s), ptr, uid);
        return -1;
    }

    si = find_svc(s, len);
    if (si) {
        if (si->ptr) {
            ALOGE("add_service('%s',%p) uid=%d - ALREADY REGISTERED, OVERRIDE\n",
                 str8(s), ptr, uid);
            svcinfo_death(bs, si);
        }
        //ptr是关键数据，可惜是void*类型，只有分析驱动的实现才能知道它的真实含义。
        si->ptr = ptr;
    } else {
        si = malloc(sizeof(*si) + (len + 1) * sizeof(uint16_t));
        if (!si) {
            ALOGE("add_service('%s',%p) uid=%d - OUT OF MEMORY\n",
                 str8(s), ptr, uid);
            return -1;
        }
        si->ptr = ptr;
        si->len = len;
        memcpy(si->name, s, (len + 1) * sizeof(uint16_t));
        si->name[len] = '\0';
        si->death.func = svcinfo_death;
        si->death.ptr = si;
        si->allow_isolated = allow_isolated;
        //这个svclist是一个list，保存了当前注册到ServiceManager中的信息。
        si->next = svclist;
        svclist = si;
    }

    binder_acquire(bs, ptr);
    /**
     * 我们希望服务进程推出后，ServiceManager能有几乎做一些清理工作，例如释放前面malloc出来的si。
     * binder_link_to_death会完成这项工作，每当有服务进程推出时，ServiceManager都会得到来自binder设备的通知。
     */ 
    binder_link_to_death(bs, ptr, &si->death);
    return 0;
}

int svcmgr_handler(struct binder_state *bs,
                   struct binder_txn *txn,
                   struct binder_io *msg,
                   struct binder_io *reply)
{
    struct svcinfo *si;
    uint16_t *s;
    unsigned len;
    void *ptr;
    uint32_t strict_policy;
    int allow_isolated;

//    ALOGI("target=%p code=%d pid=%d uid=%d\n",
//         txn->target, txn->code, txn->sender_pid, txn->sender_euid);
    // svcmgr_handler就是前面说的那个magic number，值为NULL。
    //这里要比较target是不是自己。
    if (txn->target != svcmgr_handle)
        return -1;

    // Equivalent to Parcel::enforceInterface(), reading the RPC
    // header with the strict mode policy mask and the interface name.
    // Note that we ignore the strict_policy and don't propagate it
    // further (since we do no outbound RPCs anyway).
    strict_policy = bio_get_uint32(msg);
    s = bio_get_string16(msg, &len);
    if ((len != (sizeof(svcmgr_id) / 2)) ||
        memcmp(svcmgr_id, s, sizeof(svcmgr_id))) {
        fprintf(stderr,"invalid id %s\n", str8(s));
        return -1;
    }

    switch(txn->code) {
    case SVC_MGR_GET_SERVICE://得到某个service的信息，service用字符串表示。
    case SVC_MGR_CHECK_SERVICE:
        s = bio_get_string16(msg, &len);//s是字符串表示service名称。
        ptr = do_find_service(bs, s, len, txn->sender_euid);
        if (!ptr)
            break;
        bio_put_ref(reply, ptr);
        return 0;

    case SVC_MGR_ADD_SERVICE://对应addService请求。
        s = bio_get_string16(msg, &len);
        ptr = bio_get_ref(msg);
        allow_isolated = bio_get_uint32(msg) ? 1 : 0;
        if (do_add_service(bs, s, len, ptr, txn->sender_euid, allow_isolated))
            return -1;
        break;
    //得到当前系统已经注册的所有service的名字。
    case SVC_MGR_LIST_SERVICES: {
        unsigned n = bio_get_uint32(msg);

        si = svclist;
        while ((n-- > 0) && si)
            si = si->next;
        if (si) {
            bio_put_string16(reply, si->name);
            return 0;
        }
        return -1;
    }
    default:
        ALOGE("unknown code %d\n", txn->code);
        return -1;
    }

    bio_put_uint32(reply, 0);
    return 0;
}

/**
 * ServiceManager存在的意义
 * 1. 集中管理系统内所有的服务，它能施加权限控制，并不是任何进程都能注册服务的。
 * 2. 支持通过字符串名称来查找对应的Service。这个功能很想DNS。
 * 3. 由于种种原因，server进程可能生死无常。如果让每个Client都去检测，压力实在太大了。
 * 现在有了同意的管理机构，Client只需要查询ServiceManager,就能把握动向，得到最新信息。
 * 这可能是ServiceManager存在的最大意义吧。
 */ 

int main(int argc, char **argv)
{
    struct binder_state *bs;
    //BINDER_SERVICE_MANAGER的值为NULL, 是一个magic number。
    void *svcmgr = BINDER_SERVICE_MANAGER;
    // 1. 应该是代开binder设备吧？
    bs = binder_open(128*1024);
    // 2. 成为manager，是不是把自己的handle置为0？
    if (binder_become_context_manager(bs)) {
        ALOGE("cannot become context manager (%s)\n", strerror(errno));
        return -1;
    }

    svcmgr_handle = svcmgr;
    //3. 处理客户端发过来的请求。
    binder_loop(bs, svcmgr_handler);
    return 0;
}



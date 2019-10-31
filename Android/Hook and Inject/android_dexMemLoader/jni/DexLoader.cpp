/*
*****************************************************************************
* File        :
* Description : dex_mem_loader dexƒ⁄¥Êº”‘ÿ
* Creation    : 2016.12
* Author      : ll-hack
* History     :
*
******************************************************************************
**/
#include "DexLoader.h"

typedef const void* (*org1_artDexFileOpenMemory19)(unsigned char const*base, unsigned int size, std::string const& location, unsigned int location_checksum, void* mem_map);

typedef const void* (*org_artDexFileOpenMemory21)(const uint8_t* base, size_t size, const std::string& location, uint32_t location_checksum, void* mem_map, std::string* error_msg);

typedef const void* (*org_artDexFileOpenMemory22)(const uint8_t* base, size_t size, const std::string& location, uint32_t location_checksum, void* mem_map, const void*oat_file, std::string* error_msg);

typedef const void* (*org_artDexFileOpenMemory23)(void* recookie, const uint8_t* base, size_t size, const std::string& location, uint32_t location_checksum, void* mem_map, const void*oat_dex_file, std::string* error_msg);

void* getLibartHandler(const char* libartName)
{
	return (void*)dlopen(libartName, RTLD_LAZY);
}

int sdkVersion()
{
    static bool done = false;
    static int sdkVersion = 0;
    if (!done)
    {
        done = true;
        char propValue[PROP_VALUE_MAX] = {0};
        __system_property_get("ro.build.version.sdk", propValue);
        sdkVersion = atoi(propValue);
    }

    return sdkVersion;
}

/**
 *  4.4 art
 */
const void* LoadByteArt19(void* libArthandler, const char* base, size_t size)
{
    std::string location = "";
    std::string err_msg;
	org1_artDexFileOpenMemory19 func = (org1_artDexFileOpenMemory19)dlsym(libArthandler, "_ZN3art7DexFile10OpenMemoryEPKhjRKSsjPNS_6MemMapE");
	if(!func){
		return NULL;
	}
    const Header* dex_header = reinterpret_cast<const Header*>(base);//dex buffer header
    const void* cookie = func((const unsigned char *) base, size, location, dex_header->checksum_, NULL);
	if (!cookie) {
		LDBG("LoadByte19 Failed");
    }
	else {
		LDBG("LoadByte19 : %x", cookie);
	}
	return cookie;
}

const void* LoadByteArt21(void* libArthandler, const char* base, size_t size)
{
	std::string location = "";
	std::string err_msg;
	org_artDexFileOpenMemory21 func = (org_artDexFileOpenMemory21)dlsym(libArthandler, "_ZN3art7DexFile10OpenMemoryEPKhjRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPNS_6MemMapEPS9_");
	if(!func){
		return NULL;
	}
	const Header* dex_header = reinterpret_cast<const Header*>(base);
	const void *cookie = func((const unsigned char *) base, size, location, dex_header->checksum_, NULL, &err_msg);
	if (!cookie) {
		LDBG("LoadByte21 Failed %s", err_msg.c_str());
	}
	else {
		LDBG("LoadByte21 : %x", cookie);
	}
	return cookie;
}

const void* LoadByteArt22(void* libArthandler, const char* base, size_t size)
{
	std::string location = "";
	std::string err_msg;
	//sumsung
	org_artDexFileOpenMemory22 func = (org_artDexFileOpenMemory22)dlsym(libArthandler, "_ZN3art7DexFile10OpenMemoryEPKhjRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPNS_6MemMapEPKNS_7OatFileES2_PS9_.constprop.183");
	if(!func){
		func = (org_artDexFileOpenMemory22)dlsym(libArthandler, "_ZN3art7DexFile10OpenMemoryEPKhjRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPNS_6MemMapEPKNS_7OatFileES2_PS9_");
	}
	if(!func){
		return NULL;
	}
	const Header* dex_header = reinterpret_cast<const Header*>(base);
	const void *cookie = func((const unsigned char *) base, size, location, dex_header->checksum_, NULL, NULL, &err_msg);
	if (!cookie) {
		LDBG("LoadByte22 Failed %s", err_msg.c_str());
	}
	else {
		LDBG("LoadByte22 : %x", cookie);
	}
	return cookie;
}

const void* LoadByteArt23(void* libArthandler, const char* base, size_t size)
{
	std::string location = "";
	std::string err_msg;
	void* retcookie = malloc(0x78);
	memset(retcookie, 0, 0x78);
	org_artDexFileOpenMemory23 func = (org_artDexFileOpenMemory23)dlsym(libArthandler, "_ZN3art7DexFile10OpenMemoryEPKhjRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPNS_6MemMapEPKNS_10OatDexFileEPS9_");
	if(!func){
		return NULL;
	}
	const Header* dex_header = reinterpret_cast<const Header*>(base);
	func(retcookie,(const unsigned char *) base, size, location, dex_header->checksum_, NULL, NULL, &err_msg);
	if (*(int*)retcookie==0) {
		LDBG("LoadByte23 Failed %s", err_msg.c_str());
	}
	else {
		LDBG("LoadByte23 : %x", retcookie);
	}
	return retcookie;
}




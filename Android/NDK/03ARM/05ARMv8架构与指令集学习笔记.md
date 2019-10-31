[TOC]



# ARMv8架构与指令集

## 第一章 ARMv8简介

### 1.1 基础知识

ARMv8的架构继承以往ARMv7与之前处理器技术的基础，除了现有的16/32bit的Thumb2指令支持外，也向前兼容现有的A32(ARM 32bit)指令集，基于64bit的AArch64架构，除了新增A64(ARM 64bit)指令集外，也扩充了现有的A32(ARM 32bit)和T32(Thumb2 32bit）指令集，另外还新增加了CRYPTO(加密)模块支持。

## 1.2 专业名词解释

| AArch32         | 描述 32bit Execution State                        |
| --------------- | ------------------------------------------------- |
| AArch64         | 描述 64bit Execution State                        |
| A32、T32        | AArch32 ISA （Instruction Architecture）          |
| A64             | *AArch64 ISA* （*Instruction Architecture*）      |
| Interprocessing | 描述 AArch32 和 AArch64 两种执行状态之间的切换    |
| SIMD            | Single-Instruction, Multiple-Data（单指令多数据） |



## 第二章 Excution State

### 2.1 提供两种 Execution State

ARMv8 提供 AArch32 state 和 AArch64 state 两种Execution State，下面是两种 Execution State 对比。

* AArch32
  * 提供13个32bit通用寄存器`R0-R12`，一个32bit PC指针 (`R15`)、堆栈指针`SP `(`R13`)、链接寄存器`LR `(`R14`)。
  * 提供一个32bit异常链接寄存器`ELR`, 用于`Hyp mode`下的异常返回。
  * 提供32个64bit `SIMD`向量和标量`floating-point`支持。
  * 提供两个指令集`A32`（32bit）、`T32`（16/32bit）。
  * 兼容`ARMv7`的异常模型。
  * 协处理器只支持`CP10\CP11\CP14\CP15`。

* AArch64
  * 提供31个64bit通用寄存器`X0-X30`（`W0-W30`），其中`X30`是程序链接寄存器`LR`。
  * 提供一个64bit `PC`指针、堆栈指针`SPx `、异常链接寄存器`ELRx`。
  * 提供32个128bit `SIMD`向量和标量floating-point支持。
  * 定义`ARMv8`异常等级`ELx`（x<4）,x越大等级越高，权限越大。
  * 定义一组PE state寄器PSTATE（`NZCV/DAIF/CurrentEL/SPSel`等），用于保存PE当前的状态信息。
  * 没有协处理器概念。

### 2.2 决定Execution State的条件

* `SPSR_EL1.M[4]` 决定`EL0`的执行状态，为`0` 则`64bit `,否则`32bit`。

* `HCR_EL2.RW `决定`EL1`的执行状态，为`1` 则`64bit `,否则`32bit`。

* `SCR_EL3.RW`确定`EL2 or EL1`的执行状态，为`1` 则`64bit `,否则`32bit`。

* `AArch32`和`AArch64`之间的切换只能通过发生异常或者系统`Reset`来实现.（A32 和T32之间是通过BX指令切换的）。

  ![](/pic/AArch32和AArch64切换.png)



## 第三章 Exception Level

ARMv8定义EL0-EL3共 4个Exception Level来控制PE的行为。

- `ELx（x<4）`，`x`越大等级越高，执行特权越高。
- 执行在`EL0`称为非特权执行。
- `EL2 `没有`Secure state`，只有`Non-secure state`
- `EL3 `只有`Secure state`，实现`EL0/EL1`的`Secure` 和`Non-secure`之间的切换。
- `EL0 & EL1` 必须要实现，`EL2/EL3`则是可选实现。



### 3.1 Exception Level 与Security

 **Exception Level**

- `EL0` : `Application`。
- `EL1 `: `Linux kernel- OS`。
- `EL2`: `Hypervisor` (可以理解为上面跑多个虚拟OS)。
- `EL3 `: `Secure Monitor`(`ARM Trusted Firmware`)。

**Security**

- `Non-secure` : `EL0/EL1/EL2`, 只能访问`Non-secure memory`。
- `Secure` : `EL0/EL1/EL3`, 可以访问`Non-secure memory & Secure memory`,可起到物理屏障安全隔离作用。



#### 3.1.1 EL3使用AArch64、AArch32的对比

 **Common**

- `User mode` 只执行在`Non- Secure EL0 or Secure ELO`。
- `SCR_EL3.NS`决定的是`low level EL`的`secure/non-secure`状态，不是绝对自身的。
- `EL2`只有`Non-secure state`。
- `EL0 `既有`Non-secure state` 也有`Secure state`。

**EL3 AArch64**

* 若`EL1`使用`AArch32`,那么`Non- Secure {SYS/FIQ/IRQ/SVC/ABORT/UND}` 模式执行在`Non-secure EL1`，`Secure {SYS/FIQ/IRQ/SVC/ABORT/UND}`模式执行在`Secure EL1`。
* 若 `SCR_EL3.NS == 0`,则切换到`Secure EL0/EL1`状态，否则切换到`Non-secure ELO/EL1`状态
  `Secure state` 只有`Secure EL0/EL1/EL3`。

 **EL3 AArch32**

* 若`EL1`使用`AArch32`,那么`Non- Secure {SYS/FIQ/IRQ/SVC/ABORT/UND} `模式执行在`Non-secure EL1`，`Secure {SYS/FIQ/IRQ/SVC/ABORT/UND}`模式执行在`EL3`。
* `Secure state`只有`Secure EL0/EL3`，没有`Secure EL1`，要注意和上面的情况不同。



当`EL3`使用`AArch64`时，有如下结构组合：

![](/pic/AArch64Mode.png)

当`EL3`使用`AArch32`时，有如下结构组合：

![](/pic/AArch32Mode.png)



### 3.2 ELx 和 Execution State 组合

假设`EL0-EL3`都已经实现，那么将会有如下组合：

以下两类组合不存在`64bit –> 32bit`之间的所谓 `Interprocessing `切换

> EL0/EL1/EL2/EL3  => AArch64
>
> EL0/EL1/EL2/EL3  => AArch32

以下三类组合存在`64bit –> 32bit`之间的所谓 `Interprocessing `切换

> EL0 => AARCH32，EL1/EL2/EL3 => AArch64
>
> EL0/EL1 => AArch32，EL2/EL3 => AArch64
>
> EL0/EL1/EL2 => AArch32，EL3 => AArch64

组合规则 

> 字宽（ELx）<= 字宽（EL(x+1)）  { x=0,1,2 }
>
> 原则：上层字宽不能大于底层字宽

五类经典组合图示:

![](/pic/组合图示.png)



### 3.3路由控制
如果`EL3`使用`AArch64`,则有如下异常路由控制。

#### 3.3.1 路由规则
路由规则如下图所示（`from ARMv8 Datasheet`）：

![](/pic/路由规则.png)

规则小结如下：

> 若SPSR_EL1.M[4] == 0，则决定ELO使用AArch64,否则AArch32。
>
> 若SCR_EL3.RW == 1，则决定 EL2/EL1 是使用AArch64，否则AArch32。
>
> 若SCR_EL3.{EA, FIQ, IRQ} == 1，则所有相应的SError\FIQ\IRQ 中断都被路由到EL3。
>
> 若HCR_EL2.RW == 1，则决定EL1使用AArch64，否则使用AArch32。
>
> 若HCR_EL2.{AMO, IMO, FMO} == 1，则EL1/EL0所有对应的SError\FIQ\IRQ中断都被路由到EL2,同时使能对应的虚拟中断VSE,VI,VF。
>
> 若HCR_EL2.TGE == 1，那么会忽略HCR_EL2.{AMO, IMO, FMO}的具体值，直接当成1处理，则EL1/EL0所有对应的SError\FIQ\IRQ中断都被路由到EL2，同时禁止所有虚拟中断。

**注意：** `SCR_EL3.{EA, FIQ, IRQ}bit`的优先级高于`HCR_EL2.{AMO, IMO, FMO} bit`优先级，路由优先考虑`SCR_EL3`。

#### 3.3.2 IRQ/FIQ/SError路由流程图

![](/pic/路由流程图.png)

## 第四章 ARMv8寄存器

寄存器名称描述

| 位宽   | 分类       |                |                 |
| ------ | ---------- | -------------- | --------------- |
| 32-bit | Wn（通用） | WZR（0寄存器） | WSP（堆栈指针） |
| 64-bit | Xn（通用） | XZR（0寄存器） | SP（堆栈指针）  |

### 4.1 AArch32重要寄存器


| 寄存器类型 | Bit   | 描述                                                                                                                                                                                                                           |
| ---------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| R0-R14     | 32bit | 通用寄存器，但是ARM不建议使用有特殊功能的R13，R14，R15当做通用寄存器使用.                                                                                                                                                      |
| SP_x       | 32bit | 通常称R13为堆栈指针，除了User和Sys模式外，其他各种模式下都有对应的SP_x寄存器：x ={ und/svc/abt/irq/fiq/hyp/mon}                                                                                                                |
| LR_x       | 32bit | 称R14为链接寄存器，除了User和Sys模式外，其他各种模式下都有对应的SP_x寄存器：x ={ und/svc/abt/svc/irq/fiq/mon},用于保存程序返回链接信息地址，AArch32环境下，也用于保存异常返回地址，也就说LR和ELR是公用一个，AArch64下是独立的. |
| ELR_hyp    | 32bit | Hyp mode下特有的异常链接寄存器，保存异常进入Hyp mode时的异常地址                                                                                                                                                               |
| PC         | 32bit | 通常称R15为程序计算器PC指针，AArch32 中PC指向取指地址，是执行指令地址+8，AArch64中PC读取时指向当前指令地址.                                                                                                                    |
| CPSR       | 32bit | 记录当前PE的运行状态数据,CPSR.M[4:0]记录运行模式，AArch64下使用PSTATE代替                                                                                                                                                      |
| APSR       | 32bit | 应用程序状态寄存器，EL0下可以使用APSR访问部分PSTATE值                                                                                                                                                                          |
| SPSR_x     | 32bit | 是CPSR的备份，除了User和Sys模式外，其他各种模式下都有对应的SPSR_x寄存器：x ={ und/svc/abt/irq/fiq/hpy/mon}，注意：这些模式只适用于32bit运行环境                                                                                |
| HCR        | 32bit | EL2特有，HCR.{TEG,AMO,IMO,FMO,RW}控制EL0/EL1的异常路由                                                                                                                                                                         |
| SCR        | 32bit | EL3特有，SCR.{EA,IRQ,FIQ,RW}控制EL0/EL1/EL2的异常路由，注意EL3始终不会路由                                                                                                                                                     |
| VBAR       | 32bit | 保存任意异常进入非Hyp mode & 非Monitor mode的跳转向量基地址                                                                                                                                                                    |
| HVBAR      | 32bit | 保存任意异常进入Hyp mode的跳转向量基地址                                                                                                                                                                                       |
| MVBAR      | 32bit | 保存任意异常进入Monitor mode的跳转向量基地址                                                                                                                                                                                   |
| ESR_ELx    | 32bit | 保存异常进入ELx时的异常综合信息，包含异常类型EC等，可以通过EC值判断异常class                                                                                                                                                   |
| PSTATE     |       | 不是一个寄存器，是保存当前PE状态的一组寄存器统称，其中可访问寄存器有：PSTATE.{NZCV,DAIF,CurrentEL,SPSel},属于ARMv8新增内容，主要用于64bit环境下                                                                                |

#### 4.1.1 A32状态下寄存器组织

![](/pic/A32状态下寄存器组织.png)

> 所谓的banked register 是指一个寄存器在不同模式下有对应不同的寄存器，比如SP，在abort模式下是SP_bat，在Und模式是SP_und,在iqr模式下是SP_irq等，进入各种模式后会自动切换映射到各个模式下对应的寄存器.
>
> R0-R7是所谓的非banked register，R8-R14是所谓的banked register

#### 4.1.2 T32状态下寄存器组织

| A32使用Rd/Rn编码位宽4位 | T32-32bit使用Rd/Rn编码位宽4位 | T32-16bit使用Rd/Rn编码位宽3位                                                                             |
| ----------------------- | ----------------------------- | --------------------------------------------------------------------------------------------------------- |
| R0                      | R0                            | R0                                                                                                        |
| R1                      | R1                            | R1                                                                                                        |
| R2                      | R2                            | R2                                                                                                        |
| R3                      | R3                            | R3                                                                                                        |
| R4                      | R4                            | R4                                                                                                        |
| R5                      | R5                            | R5                                                                                                        |
| R6                      | R6                            | R6                                                                                                        |
| R7                      | R7                            | R7                                                                                                        |
| R8                      | R8                            | 并不是说T32-16bit下没有R8～R12，而是有限的指令才能访问到,16bit指令的Rd/Rn编码位只有3位，所以Rx范围是R0-R7 |
| R9                      | R9                            |                                                                                                           |
| R10                     | R10                           |                                                                                                           |
| R11                     | R11                           |                                                                                                           |
| R12                     | R12                           |                                                                                                           |
| SP (R13)                | SP (R13)                      | SP (R13)                                                                                                  |
| LR (R14)                | LR (R14) //M                  | LR (R14) //M                                                                                              |
| PC (R15)                | PC (R15) //P                  | PC (R15) //P                                                                                              |
| CPSR                    | CPSR                          | CPSR                                                                                                      |
| SPSR                    | SPSR                          | SPSR                                                                                                      |

### 4.2 AArch64重要寄存器

| 寄存器类型 | Bit   | 描述                                                                                                                                       |
| ---------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| X0-X30     | 64bit | 通用寄存器，如果有需要可以当做32bit使用：WO-W30                                                                                            |
| LR (X30)   | 64bit | 通常称X30为程序链接寄存器，保存跳转返回信息地址                                                                                            |
| SP_ELx     | 64bit | 若PSTATE.M[0] ==1，则每个ELx选择SP_ELx，否则选择同一个SP_EL0                                                                               |
| ELR_ELx    | 64bit | 异常链接寄存器，保存异常进入ELx的异常地址（x={0,1,2,3}）                                                                                   |
| PC         | 64bit | 程序计数器，俗称PC指针，总是指向即将要执行的下一条指令                                                                                     |
| SPSR_ELx   | 32bit | 寄存器，保存进入ELx的PSTATE状态信息                                                                                                        |
| NZCV       | 32bit | 允许访问的符号标志位                                                                                                                       |
| DIAF       | 32bit | 中断使能位：D-Debug，I-IRQ，A-SError，F-FIQ ，逻辑0允许                                                                                    |
| CurrentEL  | 32bit | 记录当前处于哪个Exception level                                                                                                            |
| SPSel      | 32bit | 记录当前使用SP_EL0还是SP_ELx，x= {1,2,3}                                                                                                   |
| HCR_EL2    | 32bit | HCR_EL2.{TEG,AMO,IMO,FMO,RW}控制EL0/EL1的异常路由 逻辑1允许                                                                                |
| SCR_EL3    | 32bit | SCR_EL3.{EA,IRQ,FIQ,RW}控制EL0/EL1/EL2的异常路由  逻辑1允许                                                                                |
| ESR_ELx    | 32bit | 保存异常进入ELx时的异常综合信息，包含异常类型EC等.                                                                                         |
| VBAR_ELx   | 64bit | 保存任意异常进入ELx的跳转向量基地址 x={0,1,2,3}                                                                                            |
| STATE      |       | 不是一个寄存器，是保存当前PE状态的一组寄存器统称，其中可访问寄存器有：PSTATE.{NZCV,DAIF,CurrentEL,SPSel},属于ARMv8新增内容,64bit下代替CPSR |

### 4.3 64、32位寄存器的映射关系

| 64-bit   | 32-bit  |
| -------- | ------- |
| X0       | R0      |
| X1       | R1      |
| X2       | R2      |
| X3       | R3      |
| X4       | R4      |
| X5       | R5      |
| X6       | R6      |
| X7       | R7      |
| X8       | R8_usr  |
| X9       | R9_usr  |
| X20      | LR_adt  |
| X10      | R10_usr |
| X11      | R11_usr |
| X12      | R12_usr |
| X13      | SP_usr  |
| X14      | LR_usr  |
| X15      | SP_hyp  |
| X16      | LR_irq  |
| X17      | SP_irq  |
| X18      | LR_svc  |
| X19      | SP_svc  |
| X21      | SP_abt  |
| X22      | LR_und  |
| X23      | SP_und  |
| X24      | R8_fiq  |
| X25      | R9_fiq  |
| X26      | R10_fiq |
| X27      | R11_fiq |
| X28      | R12_fiq |
| X29      | SP_fiq  |
| X30(LR)  | LR_fiq  |
| SCR_EL3  | SCR     |
| HCR_EL2  | HCR     |
| VBAR_EL1 | VBAR    |
| VBAR_EL2 | HVBAR   |
| VBAR_EL3 | MVBAR   |
| ESR_EL1  | DFSR    |
| ESR_EL2  | HSR     |



## 第5章 异常模型

### 5.1 异常类型描述
#### 5.1.1 AArch32异常类型

| 异常类型               | 描述         | 默认捕获模式 | 向量地址偏移 |
| ---------------------- | ------------ | ------------ | ------------ |
| Undefined Instruction  | 未定义指令   | Und mode     | 0x04         |
| Supervisor Call        | SVC调用      | Svc mode     | 0x08         |
| Hypervisor Call        | HVC调用      | Hyp mode     | 0x08         |
| Secure Monitor Call    | SMC调用      | Mon mode     | 0x08         |
| Prefetch abort         | 预取指令终止 | Abt mode     | 0x0c         |
| Data abort             | 数据终止     | Abt mode     | 0x10         |
| IRQ interrupt          | IRQ中断      | IRQ mode     | 0x18         |
| FIQ interrupt          | FIQ中断      | FIQ mode     | 0x1c         |
| Hyp Trap exception     | Hyp捕获异常  | Hyp mode     | 0x14         |
| Monitor Trap exception | Mon捕获异常  | Mon mode     | 0x04         |

#### 5.1.2 AArch64异常类型

可分为同步异常 & 异步异常两大类,如下表描述：

**同步异常**

| 异常类型                | 描述                                                   |
| ----------------------- | ------------------------------------------------------ |
| Undefined Instruction   | 未定义指令异常                                         |
| Illegal Execution State | 非常执行状态异常                                       |
| System Call             | 系统调用指令异常（SVC/HVC/SMC）                        |
| Misaligned PC/SP        | PC/SP未对齐异常                                        |
| Instruction Abort       | 指令终止异常                                           |
| Data Abort              | 数据终止异常                                           |
| Debug exception         | 软件断点指令/断点/观察点/向量捕获/软件单步 等Debug异常 |

**异步异常**

| 类型              | 描述                           |
| ----------------- | ------------------------------ |
| SError or vSError | 系统错误类型，包括外部数据终止 |
| IRQ or vIRQ       | 外部中断 or 虚拟外部中断       |
| FIQ or vFIQ       | 快速中断 or 虚拟快速中断       |

|                      |      向量地址偏移表                 |向量地址偏移表|向量地址偏移表|向量地址偏移表|
| ---------------------------------- | --------------------- | ------------ | ------------ | ------------------ |
| 异常进入满足以下条件               | Synchronous(同步异常) | IRQ\|\| vIRQ | FIQ\|\| vFIQ | SError\|\| vSError |
| SP => SP_EL0 && 从Current EL来     | 0x000                 | 0x080        | 0x100        | 0x180              |
| SP => SP_ELx && 从Current EL来     | 0x200                 | 0x280        | 0x300        | 0x380              |
| 64bit => 64bit && 从Low level EL来 | 0x400                 | 0x480        | 0x500        | 0x580              |
| 32bit => 64bit && 从Low level EL来 | 0x600                 | 0x680        | 0x700        | 0x780              |

> SP => SP_EL0,表示使用SP_EL0堆栈指针，由PSTATE.SP == 0决定,PSTATE.SP == 1 则SP_ELx；
> 32bit => 64bit 是指发生异常时PE从AArch32切换到AArch64的情况；

### 5.2异常处理逻辑
#### 5.2.1 寄存器操作

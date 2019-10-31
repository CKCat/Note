	.arch armv5te
	.fpu softvfp
	.eabi_attribute 20, 1
	.eabi_attribute 21, 1
	.eabi_attribute 23, 3
	.eabi_attribute 24, 1
	.eabi_attribute 25, 1
	.eabi_attribute 26, 2
	.eabi_attribute 30, 6
	.eabi_attribute 34, 0
	.eabi_attribute 18, 4
	.file	"hello.c"
	.section	.rodata
	.align	2
.LC0:
	.ascii	"Hello World!\015\000"
	.text
	.align	2
	.global	main
	.type	main, %function
main:
	@ args = 0, pretend = 0, frame = 8
	@ frame_needed = 1, uses_anonymous_args = 0
	stmfd	sp!, {r4-r7, fp, lr}
	ldr r4, [sp, #-4]
	str r4, [sp]
	mov r5, #2
	ldr r4, [sp, r5, lsl #2]
	mov r5, sp 
	ldr r4, [r5, #4]!
	ldr r4, [r5], #4
	ldrb r4, [r5], #2
	strb r4, [sp, #-4]
	mov r5, sp 
	ldrsb r4, [r5]
	ldrsh  r4, [r5]
	ldrh r4, [r5]
	strh r4, [r5, #-4]
	mov r4, #0xffffffff
	mov r5, sp
	add r5, #-4
	swp r4, r4, [r5]	
	swpb r4, r0, [r5]	@ r4=byte ptr[r5], byte ptr[r5]=r0

	@ mvn
	mvn r0, #0xff	@r0 = 0xffffff00
	mvn r1, r2		@r1 = ~r2

	@ adc 
	add r0, r0, r4
	adc r1, r1, r3	@实现64位加法

	@sbc
	eor r0, r0
	subs r0, r0, r4
	sbc r1, r1, r3	@实现64位减法

	@ rsb和rsc 
	rsbs r2, r0, #0 
	rsc r3, r1, #0	@实现64位取反

	@ mla 
	mla r0, r1, r2, r3	@ r0 = r1*r2 + r3

	@ umull 和 umlal
	umull r0, r1, r2, r3	@ (r1, r0) = r2*r3
	umlal r0, r1, r2, r3	@ (r1, r0) = r2*r3 + (r1, r0)

	@ smull 和 smlal 
	smull r0, r1, r2, r3	@ (r1, r0) = r2*r3	有符号
	smlal r0, r1, r2, r3	@ (r1, r0) = r2*r3 + (r1, r0)	有符号

	@ @ smlad 和 smlsd
	@ smlad r0, r1, r2, r3
	@ smlsd r0, r1, r2, r3

	@ 位移和逻辑
	asr r0, r1, #2
	lsr r0, r1, #2
	and r0, r0, #1
	mov r0, #0xffffffff
	orr r0, r0, #0x0f
	bic r0, r0, #0x0f
	lsl r0,r1, #2
	ror r1, r1, #1  
	rrx r1, r1
	mov r0, #0
	cmp r0, #0
	mov r0, #0xffffffff
	cmn r0, #1 
	tst r0, #0


	mrs r0, CPSR
	MOV R0, #-1
	MSR CPSR_cxsf, R0
	
	mov r0, #0 
	mov r7, #1 
	swi #0



	add	fp, sp, #4
	sub	sp, sp, #8
	str	r0, [fp, #-8]
	str	r1, [fp, #-12]
	ldr	r3, .L3
.LPIC0:
	add	r3, pc, r3
	mov	r0, r3
	bl	puts(PLT)
	mov	r3, #0
	mov	r0, r3
	sub	sp, fp, #4
	@ sp needed
	ldmfd	sp!, {r4-r7, fp, pc}
.L4:
	.align	2
.L3:
	.word	.LC0-(.LPIC0+8)
	.size	main, .-main
	.ident	"GCC: (GNU) 4.9.x 20150123 (prerelease)"
	.section	.note.GNU-stack,"",%progbits

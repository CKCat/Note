	.cpu generic+fp+simd
	.file	"hello.c"
	.text
	.align	2
	.global	add
	.type	add, %function
add:
	sub	sp, sp, #32
	str	w0, [sp,12]
	str	w1, [sp,8]
	str	wzr, [sp,28]
	ldr	w0, [sp,12]
	ldr	w1, [sp,8]
#APP
// 13 "hello.c" 1
	ADD x0, x0, x1
// 0 "" 2
#NO_APP
	str	w0, [sp,28]
	ldr	w0, [sp,28]
	add	sp, sp, 32
	ret
	.size	add, .-add
	.section	.rodata
	.align	3
.LC0:
	.string	"Result of %d + %d = %d\n"
	.text
	.align	2
	.global	main
	.type	main, %function
main:
	stp	x29, x30, [sp, -32]!
	add	x29, sp, 0
	mov	w0, 1
	str	w0, [x29,28]
	mov	w0, 2
	str	w0, [x29,24]
	str	wzr, [x29,20]
	ldr	w0, [x29,28]
	ldr	w1, [x29,24]
	bl	add
	str	w0, [x29,20]
	mov w1, 2
	mov w2, 2
	cmn w0, -3
	beq L1
	add w0, w1, w2
L1:
	mneg w0, w1, w2
	neg w0, w1
	MOV X1, #0x800
	BIC X0, X0, X1
	add w0, w1, w2, lsl #3	// W0 = W1 + (W2 << 3)
	adrp	x0, .LC0
	add	x0, x0, :lo12:.LC0
	ldr	w1, [x29,28]
	ldr	w2, [x29,24]
	ldr	w3, [x29,20]
	bl	printf
	ldp	x29, x30, [sp], 32
	ret
	.size	main, .-main
	.ident	"GCC: (GNU) 4.9.x 20150123 (prerelease)"
	.section	.note.GNU-stack,"",%progbits

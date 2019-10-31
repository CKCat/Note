	.cpu generic+fp+simd
	.file	"hello.c"
	.section	.rodata
	.align	3
.LC0:
	.string	"Hello World!\r"
	.text
	.align	2
	.global	main
	.type	main, %function
main:
	stp	x29, x30, [sp, -32]!
	add	x29, sp, 0
	str	w0, [x29,28]
	str	x1, [x29,16]
	adrp	x0, .LC0
	add	x0, x0, :lo12:.LC0
	bl	puts
	mov	w0, 0
	ldp	x29, x30, [sp], 32
	ret
	.size	main, .-main
	.ident	"GCC: (GNU) 4.9.x 20150123 (prerelease)"
	.section	.note.GNU-stack,"",%progbits

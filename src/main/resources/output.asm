.data
_x_: .word 0
_y_: .word 0

.text
.globl main
main:
  # Simplemente llamamos a syscall 10 para terminar
  li $v0, 10
  syscall

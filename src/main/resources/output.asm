.data
_globalVar1_: .word 0
_counter_: .word 0
_msgCounter_: .word 0
_sum_: .word 0
_flag_: .word 0
_msgFlagTrue_: .word 0
_msgFlagFalse_: .word 0

.text
.globl main
main:
  # Simplemente llamamos a syscall 10 para terminar
  li $v0, 10
  syscall

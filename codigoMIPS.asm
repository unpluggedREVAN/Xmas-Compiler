.data

.text
.globl main
main:
main:
# Reserva para variable _x_
# Reserva para variable _y_
# Cargar literal entero 10
li $t2, 10
sw $t2, _y_
# Acceso a variable _y_
lw $t2, _y_
lw $a0, _y_
li $v0, 1
syscall
# Reserva para variable _pi_
# Cargar literal float 3.14
li.s $f0, 3.14
mov.s $f2, $f0
mfc1 $t2, $f2
sw $t2, _pi_
# Acceso a literal string Pi = 
la $a0, "Pi = "
li $v0, 4
syscall
# Acceso a variable _pi_
lw $t2, _pi_
lw $a0, _pi_
li $v0, 1
syscall
# Inicio de bloque
# Fin de bloque
li $v0, 10
syscall

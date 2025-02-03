.data
_x_: .word 0
_y_: .word 10
_pi_: .float 3.14

.text
main:
# Reserva para variable _x_
# Reserva para variable _y_
# Cargar literal entero 10
li $t2, 10
# Asignación a variable _y_
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
# Asignación a variable _pi_
s.s $f0, _pi_
# Acceso a literal string Pi = 
la $a0, "Pi = "
li $v0, 4
syscall
# Acceso a variable _pi_
lw $t2, _pi_
l.s $f12, _pi_
li $v0, 2
syscall
# Inicio de bloque
# Fin de bloque

# Fin del programa - syscall de salida
li $v0, 10
syscall

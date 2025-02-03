.data
_var1_: .float 0.0
_var2_: .word 22
_var3_: .word 15

.text
main:
# Reserva para variable _var1_
# Reserva para variable _var2_
# Cargar literal entero 22
li $t2, 22
# Asignación a variable _var2_
sw $t2, _var2_
# Reserva para variable _var3_
# Cargar literal entero 15
li $t2, 15
# Asignación a variable _var3_
sw $t2, _var3_
# Acceso a variable _var2_
lw $t2, _var2_
move $v0, _var2_
j exit_function
# Inicio de bloque
# Fin de bloque

# Fin del programa - syscall de salida
li $v0, 10
syscall

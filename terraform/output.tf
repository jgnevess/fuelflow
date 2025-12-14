output "instancia_ip" {
  description = "IP público da instância"
  value       = aws_instance.instancia.public_ip
}

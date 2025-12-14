data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

resource "aws_key_pair" "fuelflow_key" {
  key_name   = "fuelflow-key-${formatdate("YYYYMMDDHHmmss", timestamp())}"
  public_key = file("~/.ssh/id_rsa_terraform.pub")
}

resource "aws_security_group" "sg-fuelflow" {
  vpc_id = data.aws_vpc.default.id

  ingress {
    description = "SSH"
    from_port   = 7642
    to_port     = 7642
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "API"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "sg-fuelflow"
  }
}

resource "aws_instance" "instancia" {
  ami                    = "ami-0360c520857e3138f"
  instance_type          = "t3.micro"
  key_name               = aws_key_pair.fuelflow_key.key_name
  vpc_security_group_ids = [aws_security_group.sg-fuelflow.id]

  user_data = <<-EOF
    #!/bin/bash
    set -e

    # Atualiza sistema
    apt update -y
    apt upgrade -y

    # Pacotes base
    apt install -y \
      ca-certificates \
      curl \
      gnupg \
      lsb-release \
      unzip \
      fail2ban \
      nginx

    systemctl enable fail2ban
    systemctl start fail2ban
    systemctl enable nginx
    systemctl start nginx

    # Docker
    curl -fsSL https://get.docker.com | sh
    systemctl enable docker
    systemctl start docker

    # Docker Compose plugin
    mkdir -p /usr/local/lib/docker/cli-plugins
    curl -SL https://github.com/docker/compose/releases/download/v2.27.0/docker-compose-linux-x86_64 \
      -o /usr/local/lib/docker/cli-plugins/docker-compose
    chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

    # Certbot (via snap, jeito menos doloroso)
    snap install core
    snap refresh core
    snap install --classic certbot
    ln -s /snap/bin/certbot /usr/bin/certbot

  EOF

  tags = {
    Name = "UbuntuServer"
  }
}

resource "aws_key_pair" "bastion_key" {
  key_name = "webinar_kafka_ops_bastion_key"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDAjw1p9kvb9dMaZFz9Wx+/o5MIebIp/CtWdnpl6K3QIM2ICCM+aWGMHN6asHraTNedMsCE/NXVf6m2y4/pjA8dR5fs22fdkBWbWLYyDSWEXCme+jyioZq7tWA4vGyJbeBK5en4UYQjHVf+CHL+/SGVUPksTt4prByPe171M1SRRvd6yDQGKoDKqosjIslew0JKosbxinZn8gNmdSFuidCZXFhnqUfsFVWrR0lC9Kkg147Bj4erX+7ZKfdi3+5GzTV6AzFoeg4RRqMnFkGiaUm5gAZkA4yS+ILilQWFZJeIxiwwf1xhKu6iEJPm7PVo41c6m9C1V3pmQf7J6kRxIGaDXPteO4GYn75dk5mHB2+aly7ZkCraZfbcN2rh2NcAmWTP7Dc71b1CbDqpdHIhVnL7J+3pO2+EO7lNjNVea/Exev7SeyDvLEf05+e3SAyORAUostgXmsbYEJbuxKOhpSkuxjRnvEpIbZ4weDYeZZmBO1b5x4Vd9aQfgpQN6boFUCs= csotlac@gmail.com"
  tags = local.tags
}

resource "aws_internet_gateway" "igw" {
  vpc_id = var.private_vpc_id
  tags = local.tags
}

resource "aws_route_table" "public_route_table_az1" {
  vpc_id = var.private_vpc_id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
  tags = local.tags
}
resource "aws_route_table_association" "public_rt_association_az1" {
  route_table_id = aws_route_table.public_route_table_az1.id
  subnet_id = var.subnet_az_1
}

resource "aws_route_table" "public_route_table_az2" {
  vpc_id = var.private_vpc_id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
  tags = local.tags
}
resource "aws_route_table_association" "public_rt_association_az2" {
  route_table_id = aws_route_table.public_route_table_az2.id
  subnet_id = var.subnet_az_2
}

resource "aws_route_table" "public_route_table_az3" {
  vpc_id = var.private_vpc_id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
  tags = local.tags
}
resource "aws_route_table_association" "public_rt_association_az3" {
  route_table_id = aws_route_table.public_route_table_az3.id
  subnet_id = var.subnet_az_3
}

resource "aws_security_group" "bastion_sg" {
  name = "webinar-kafka-ops Bastion Security Group"
  description = "Allow 8080 from anywhere"
  vpc_id = var.private_vpc_id
  ingress {
    from_port = 8080
    protocol = "tcp"
    to_port = 8080
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port = 0
    protocol = "-1"
    to_port = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = local.tags
}
resource "aws_instance" "bastion_host" {
  ami           = "ami-03c3a7e4263fd998c"
  instance_type = "t2.micro"
  subnet_id = var.subnet_az_1
  associate_public_ip_address = true
  security_groups = [aws_security_group.bastion_sg.id]
  key_name = "webinar_kafka_ops_bastion_key"
  tags = local.tags
}
resource "aws_eip" "nat_gateway_eip" {
  vpc = true
  tags = local.tags
}
resource "aws_nat_gateway" "nat_gateway" {
  allocation_id = aws_eip.nat_gateway_eip.id
  subnet_id = var.subnet_az_1
  tags = local.tags
}
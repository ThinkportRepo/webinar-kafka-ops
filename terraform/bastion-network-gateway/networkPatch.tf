resource "aws_key_pair" "bastion_key" {
  key_name = "webinar_kafka_ops_bastion_key"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDAjw1p9kvb9dMaZFz9Wx+/o5MIebIp/CtWdnpl6K3QIM2ICCM+aWGMHN6asHraTNedMsCE/NXVf6m2y4/pjA8dR5fs22fdkBWbWLYyDSWEXCme+jyioZq7tWA4vGyJbeBK5en4UYQjHVf+CHL+/SGVUPksTt4prByPe171M1SRRvd6yDQGKoDKqosjIslew0JKosbxinZn8gNmdSFuidCZXFhnqUfsFVWrR0lC9Kkg147Bj4erX+7ZKfdi3+5GzTV6AzFoeg4RRqMnFkGiaUm5gAZkA4yS+ILilQWFZJeIxiwwf1xhKu6iEJPm7PVo41c6m9C1V3pmQf7J6kRxIGaDXPteO4GYn75dk5mHB2+aly7ZkCraZfbcN2rh2NcAmWTP7Dc71b1CbDqpdHIhVnL7J+3pO2+EO7lNjNVea/Exev7SeyDvLEf05+e3SAyORAUostgXmsbYEJbuxKOhpSkuxjRnvEpIbZ4weDYeZZmBO1b5x4Vd9aQfgpQN6boFUCs= csotlac@gmail.com"
  tags = local.tags
}

resource "aws_security_group" "bastion_sg" {
  name = "webinar-kafka-ops Bastion Security Group"
  description = "Allow 22 from anywhere"
  vpc_id = var.public_vpc_id
  ingress {
    from_port = 22
    protocol = "tcp"
    to_port = 22
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
  subnet_id = var.public_subnet_id
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
  subnet_id = var.public_subnet_id
  tags = local.tags
}
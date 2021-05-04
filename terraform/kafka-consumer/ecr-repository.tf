resource "aws_ecr_repository" "repo-consumer" {
  name                 = "webinar-kafka-ops-consumer"
  image_tag_mutability = "IMMUTABLE"
  tags                 = local.tags
}

resource "aws_ecr_repository_policy" "repo-consumer-policy" {
  repository = aws_ecr_repository.repo-consumer.name
  policy     = <<EOF
  {
    "Version": "2008-10-17",
    "Statement": [
      {
        "Sid": "adds full ecr access to the demo repository",
        "Effect": "Allow",
        "Principal": "*",
        "Action": [
          "ecr:BatchCheckLayerAvailability",
          "ecr:BatchGetImage",
          "ecr:CompleteLayerUpload",
          "ecr:GetDownloadUrlForLayer",
          "ecr:GetLifecyclePolicy",
          "ecr:InitiateLayerUpload",
          "ecr:PutImage",
          "ecr:UploadLayerPart"
        ]
      }
    ]
  }
  EOF
}
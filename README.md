# Recipy Backend

This is the backend service for my new home projekt "recipy". Recipy can hold recipes and make
random suggestions for a weekly cooking plan.

# Raspberry Pi

The following section explains how to build this for a raspberry pi 4 running a 64bit version of
raspbian.

## Setup

On a fresh installed raspbian first update apt-get:

```bash
sudo apt-get update && sudo apt-get upgrade
```

### Install Terraform

Run following commands on the raspberry.

```bash
sudo apt install snapd
sudo reboot
sudo snap install core
sudo snap install terraform --candidate --classic
```

### Install Docker

Run following commands on the raspberry. This is for the user pi.

```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker pi
dockerd-rootless-setuptool.sh install
sudo reboot
```

### Create directories

On the raspberry run:

```bash
mkdir -p ~/recipy/terraform
```

### Copy terraform scripts

On the host run:

```bash
scp -r ./terraform/* pi@recipy-server.local:/home/pi/recipy/terraform/
```

## Build and push the docker image

On the host run:

```bash
docker buildx build --platform linux/arm64/v8 -t chauss/recipy-backend:alpha-0.0.1 .
docker push chauss/recipy-backend:alpha-0.0.1
```

## Run

On the raspberry run:

```bash
cd ~/recipy/terraform
terraform init
terraform apply
```



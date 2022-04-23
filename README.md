# Recipy Backend

This is the backend service for my new home projekt "recipy". Recipy can hold recipes and make
random suggestions for a weekly cooking plan.

## Building the docker image

```shell
docker build -t recipy-backend .
```

## Run on raspberry

1. Build the application

```shell
./mvnw clean package
```

2. Build the image for the right platform

```shell
docker buildx build --no-cache --platform <platform> -t recipy-backend .
```

3. Copy the image and terraform to the raspberry

```shell
docker save -o ./recipy-backend-image.tar recipy-backend
scp ./recipy-backend-image.tar pi@raspberrypi:/home/pi/recipy/recipy-backend-image.tar
scp -r ./terraform pi@raspberrypi:/home/pi/recipy/terraform
```

4. On the raspberry load the docker image and run terraform

```shell
docker load ~/recipy/recipy-backend-image.tar
cd ~/recipy/terraform
terraform apply
```

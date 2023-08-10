/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <iostream>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>

#define SET_CUR_VALUE 0
#define TOUCH_PEN_MODE 20
#define TOUCH_MAGIC 't'
#define TOUCH_IOC_SETMODE _IO(TOUCH_MAGIC, SET_CUR_VALUE)
#define TOUCH_DEV_PATH "/dev/xiaomi-touch"
#define TOUCH_ID 0

int main(int argc, char **argv) {
    if(argc != 2) {
        fprintf(stderr, "Usage: %s <value>\n", argv[0]);
        return -1;
    }
    int fd = open(TOUCH_DEV_PATH, O_RDWR);
    int arg[3] = {TOUCH_ID, TOUCH_PEN_MODE, atoi(argv[1])};
    ioctl(fd, TOUCH_IOC_SETMODE, &arg);
    close(fd);
}
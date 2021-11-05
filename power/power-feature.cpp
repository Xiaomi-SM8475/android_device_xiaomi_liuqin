/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <aidl/vendor/aospa/power/BnPowerFeature.h>
#include <android-base/file.h>
#include <android-base/logging.h>
#include <sys/ioctl.h>

#define SET_CUR_VALUE 0
#define TOUCH_DOUBLETAP_MODE 14
#define TOUCH_MAGIC 't'
#define TOUCH_IOC_SETMODE _IO(TOUCH_MAGIC, SET_CUR_VALUE)
#define TOUCH_DEV_PATH "/dev/xiaomi-touch"
#define TOUCH_ID 0

namespace aidl {
namespace vendor {
namespace aospa {
namespace power {

bool setDeviceSpecificFeature(Feature feature, bool enabled) {
    switch (feature) {
        case Feature::DOUBLE_TAP: {
            int fd = open(TOUCH_DEV_PATH, O_RDWR);
            int arg[3] = {TOUCH_ID, TOUCH_DOUBLETAP_MODE, enabled ? 1 : 0};
            ioctl(fd, TOUCH_IOC_SETMODE, &arg);
            close(fd);
            return true;
        }
        default:
            return false;
    }
}

}  // namespace power
}  // namespace aospa
}  // namespace vendor
}  // namespace aidl
#
# Copyright (C) 2023 Paranoid Android
#
# SPDX-License-Identifier: Apache-2.0
#

LOCAL_PATH := $(call my-dir)

ifneq ($(filter liuqin,$(TARGET_DEVICE)),)

include $(call all-makefiles-under,$(LOCAL_PATH))

endif

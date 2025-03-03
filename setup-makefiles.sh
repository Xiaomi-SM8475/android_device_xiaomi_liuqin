#!/bin/bash
#
# SPDX-FileCopyrightText: 2016 The CyanogenMod Project
# SPDX-FileCopyrightText: 2017-2024 The LineageOS Project
# SPDX-License-Identifier: Apache-2.0
#

set -e

DEVICE=liuqin
VENDOR=xiaomi

# Load extract_utils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "${MY_DIR}" ]]; then MY_DIR="${PWD}"; fi

ANDROID_ROOT="${MY_DIR}/../../.."

HELPER="${ANDROID_ROOT}/tools/extract-utils/extract_utils.sh"
if [ ! -f "${HELPER}" ]; then
    echo "Unable to find helper script at ${HELPER}"
    exit 1
fi
source "${HELPER}"

function vendor_imports() {
    cat <<EOF >>"$1"
		"vendor/qcom/common/system/telephony",
		"vendor/qcom/common/vendor/adreno-s",
		"vendor/qcom/common/vendor/display/5.10",
        "vendor/qcom/common/vendor/media",
EOF
}

function lib_to_package_fixup_vendor_variants() {
    if [ "$2" != "vendor" ]; then
        return 1
    fi

    case "$1" in
        audio.primary.taro | \
        libsdmextension)
            echo "$1_liuqin"
            ;;
        vendor.qti.hardware.limits@1.[0-1] | \
        vendor.qti.hardware.ListenSoundModel@1.0 | \
        vendor.qti.hardware.wifidisplaysession@1.0 | \
        vendor.xiaomi.hardware.mlipay@1.[0-1] | \
        vendor.xiaomi.hardware.mtdservice@1.0 | \
        vendor.xiaomi.hw.touchfeature@1.0)
            echo "$1_vendor"
            ;;
        *)
            return 1
            ;;
    esac
}

function lib_to_package_fixup() {
    lib_to_package_fixup_clang_rt_ubsan_standalone "$1" ||
        lib_to_package_fixup_proto_3_9_1 "$1" ||
        lib_to_package_fixup_vendor_variants "$@"
}

# Initialize the helper
setup_vendor "${DEVICE}" "${VENDOR}" "${ANDROID_ROOT}"

# Warning headers and guards
write_headers

write_makefiles "${MY_DIR}/proprietary-files.txt"

# Finish
write_footers

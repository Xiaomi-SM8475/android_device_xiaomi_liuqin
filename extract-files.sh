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

# Default to sanitizing the vendor folder before extraction
CLEAN_VENDOR=true

ONLY_FIRMWARE=
KANG=
SECTION=
CARRIER_SKIP_FILES=()

while [ "${#}" -gt 0 ]; do
    case "${1}" in
        --only-firmware)
            ONLY_FIRMWARE=true
            ;;
        -n | --no-cleanup)
            CLEAN_VENDOR=false
            ;;
        -k | --kang)
            KANG="--kang"
            ;;
        -s | --section)
            SECTION="${2}"
            shift
            CLEAN_VENDOR=false
            ;;
        *)
            SRC="${1}"
            ;;
    esac
    shift
done

if [ -z "${SRC}" ]; then
    SRC="adb"
fi

function blob_fixup() {
    case "${1}" in
        vendor/bin/hw/android.hardware.security.keymint-service-qti | vendor/lib64/libqtikeymint.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --add-needed "android.hardware.security.rkp-V1-ndk_platform.so" "${2}"
            ;;
        vendor/bin/hw/dolbycodec2 | vendor/bin/hw/vendor.dolby.hardware.dms@2.0-service | vendor/bin/hw/vendor.dolby.media.c2@1.0-service)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --add-needed "libstagefright_foundation-v33.so" "${2}"
            ;;
        vendor/etc/media_codecs.xml|vendor/etc/media_codecs_cape.xml|vendor/etc/media_codecs_cape_vendor.xml)
            sed -Ei "/media_codecs_(google_audio|google_c2|google_telephony|vendor_audio)/d" "${2}"
            ;;
        vendor/etc/camera/liuqin_enhance_motiontuning.xml | vendor/etc/camera/liuqin_motiontuning.xml)
            [ "$2" = "" ] && return 0
            sed -i 's/<?xml=/<?xml /g' "${2}"
            ;;
        vendor/etc/camera/pureShot_parameter.xml | vendor/etc/camera/pureView_parameter.xml)
            [ "$2" = "" ] && return 0
            sed -i 's/=\([0-9]\+\)>/="\1">/g' "${2}"
            ;;
        vendor/lib64/c2.dolby.client.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --add-needed "libcodec2_hidl_shim.so" "${2}"
            ;;
        vendor/lib64/libQnnGpu.so)
            [ "$2" = "" ] && return 0
            "${ANDROID_ROOT}"/prebuilts/clang/host/linux-x86/clang-r450784e/bin/llvm-strip "${2}"
            ;;
        vendor/lib64/hw/audio.primary.taro.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libstagefright_foundation.so" "libstagefright_foundation-v33.so" "${2}"
            ;;
        vendor/lib64/hw/vendor.xiaomi.sensor.citsensorservice@2.0-impl.so)
            [ "$2" = "" ] && return 0
            sed -i 's/_ZN13DisplayConfig10ClientImpl13ClientImplGetENSt3__112basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEPNS_14ConfigCallbackE/_ZN13DisplayConfig10ClientImpl4InitENSt3__112basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEPNS_14ConfigCallbackE\x0\x0\x0\x0\x0\x0\x0\x0\x0\x0/g' "${2}"
            ;;
        vendor/lib64/vendor.libdpmframework.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --add-needed "libhidlbase_shim.so" "${2}"
            ;;
        *)
            return 1
            ;;
    esac

    return 0
}

function blob_fixup_dry() {
    blob_fixup "$1" ""
}

# Initialize the helper
setup_vendor "${DEVICE}" "${VENDOR}" "${ANDROID_ROOT}" false "${CLEAN_VENDOR}"

extract "${MY_DIR}/proprietary-files.txt" "${SRC}" "${KANG}" --section "${SECTION}"

"${MY_DIR}/setup-makefiles.sh"

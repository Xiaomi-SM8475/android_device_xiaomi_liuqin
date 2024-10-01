#!/usr/bin/env -S PYTHONPATH=../../../tools/extract-utils python3
#
# SPDX-FileCopyrightText: 2024 The LineageOS Project
# SPDX-License-Identifier: Apache-2.0
#

from extract_utils.fixups_blob import (
    blob_fixup,
    blob_fixups_user_type,
)
from extract_utils.fixups_lib import (
    lib_fixups,
    lib_fixups_user_type,
)
from extract_utils.main import (
    ExtractUtils,
    ExtractUtilsModule,
)

namespace_imports = [
    'vendor/qcom/common/system/telephony',
    'vendor/qcom/common/vendor/adreno-s',
    'vendor/qcom/common/vendor/display/5.10',
    'vendor/qcom/common/vendor/media/5.10',
]


def lib_fixup_liuqin_suffix(lib: str, partition: str, *args, **kwargs):
    return f'{lib}_liuqin' if partition == 'vendor' else None

def lib_fixup_vendor_suffix(lib: str, partition: str, *args, **kwargs):
    return f'{lib}_vendor' if partition == 'vendor' else None


lib_fixups: lib_fixups_user_type = {
    **lib_fixups,
    (
        'audio.primary.taro',
        'libsdmextension',
        'libqcodec2_base',
        'libqcodec2_basecodec',
        'libqcodec2_core',
        'libqcodec2_filterbase',
        'libqcodec2_hooks',
        'libqcodec2_mockfilter',
        'libqcodec2_mockqc2filter',
        'libqcodec2_platform',
        'libqcodec2_utils',
        'libqcodec2_v4l2codec',
    ): lib_fixup_liuqin_suffix,
    (
        'vendor.qti.hardware.limits@1.0',
        'vendor.qti.hardware.limits@1.1',
        'vendor.qti.hardware.ListenSoundModel@1.0',
        'vendor.qti.hardware.wifidisplaysession@1.0',
        'vendor.xiaomi.hardware.mlipay@1.0',
        'vendor.xiaomi.hardware.mlipay@1.1',
        'vendor.xiaomi.hardware.mtdservice@1.0',
        'vendor.xiaomi.hw.touchfeature@1.0',
    ): lib_fixup_vendor_suffix,
}


blob_fixups: blob_fixups_user_type = {
    (
        'vendor/bin/hw/android.hardware.security.keymint-service-qti',
        'vendor/lib64/libqtikeymint.so',
    ): blob_fixup()
        .add_needed('android.hardware.security.rkp-V1-ndk_platform.so'),
    (
        'vendor/bin/hw/dolbycodec2',
        'vendor/bin/hw/vendor.dolby.hardware.dms@2.0-service',
        'vendor/bin/hw/vendor.dolby.media.c2@1.0-service',
    ): blob_fixup()
        .add_needed('libstagefright_foundation-v33.so'),
    (
       'vendor/etc/audio/sku_cape/mixer_paths_overlay_static.xml',
    ): blob_fixup()
        .regex_replace('.+TL-handset.txt.+\n', ''),
    (
       'vendor/etc/media_codecs.xml',
       'vendor/etc/media_codecs_cape.xml',
       'vendor/etc/media_codecs_cape_vendor.xml',
    ): blob_fixup()
        .regex_replace('.+media_codecs_(google_audio|google_c2|google_telephony|vendor_audio).+\n', ''),
    (
        'vendor/etc/camera/liuqin_enhance_motiontuning.xml',
        'vendor/etc/camera/liuqin_motiontuning.xml',
    ): blob_fixup()
        .regex_replace('xml=version', 'xml version'),
    (
        'vendor/etc/camera/pureShot_parameter.xml',
        'vendor/etc/camera/pureView_parameter.xml',
    ): blob_fixup()
        .regex_replace(r'=([0-9]+)>', r'="\1">'),
    (
        'vendor/lib64/c2.dolby.client.so',
    ): blob_fixup()
        .add_needed('libcodec2_hidl_shim.so'),
    (
        'vendor/lib64/libqcodec2_core.so',
    ): blob_fixup()
        .add_needed('libcodec2_shim.so'),
    (
        'vendor/lib64/libQnnGpu.so',
    ): blob_fixup()
        .strip_debug_sections(),
    (
        'vendor/lib64/vendor.libdpmframework.so',
    ): blob_fixup()
        .add_needed('libhidlbase_shim.so'),
    (
        'vendor/lib64/hw/audio.primary.taro.so',
    ): blob_fixup()
        .replace_needed(
            'libstagefright_foundation.so',
            'libstagefright_foundation-v33.so'
        ),
    (
        'vendor/lib64/hw/vendor.xiaomi.sensor.citsensorservice@2.0-impl.so',
    ): blob_fixup()
        .binary_regex_replace(
            b'_ZN13DisplayConfig10ClientImpl13ClientImplGetENSt3__112basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEPNS_14ConfigCallbackE',
            b'_ZN13DisplayConfig10ClientImpl4InitENSt3__112basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEPNS_14ConfigCallbackE\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00',
        ),
}

module = ExtractUtilsModule(
    'liuqin',
    'xiaomi',
    blob_fixups=blob_fixups,
    lib_fixups=lib_fixups,
    namespace_imports=namespace_imports,
)

if __name__ == '__main__':
    utils = ExtractUtils.device(module)
    utils.run()

import { message } from 'antd';

// 图片类型常量
const IMAGE_TYPES = ['image/bmp', 'image/png', 'image/jpeg', 'image/jpg', 'image/gif'];

// 图片大小限制（10MB）
const IMAGE_MAX_SIZE = 10 * 1024 * 1024;

// 音频类型常量
const VOICE_TYPES = ['audio/mp3', 'audio/wma', 'audio/wav', 'audio/amr', 'audio/mpeg'];

// 音频大小限制（2MB）
const VOICE_MAX_SIZE = 2 * 1024 * 1024;

// 视频类型常量
const VIDEO_TYPES = ['video/mp4'];

// 视频大小限制（10MB）
const VIDEO_MAX_SIZE = 10 * 1024 * 1024;

/**
 * 获取上传提示文本
 * @param acceptFileTypes 接受文件类型
 * @param currentMaterialType 当前素材类型
 * @returns 上传提示文本
 */
export const getUploadTips = (acceptFileTypes: string, currentMaterialType: string) => {
  if (currentMaterialType === 'image') {
    return '支持 BMP、PNG、JPEG、JPG、GIF 格式，文件大小不超过 10MB';
  }
  if (currentMaterialType === 'voice') {
    return '支持 mp3/wma/wav/amr 格式，文件大小不超过 2M，播放长度不超过60秒';
  }
  return `支持上传 ${acceptFileTypes.replace('/*', '')} 类型的文件`;
};

/**
 * 获取当前素材类型的接受文件类型
 * @param currentMaterialType 当前素材类型
 * @param acceptFileTypes 接受文件类型
 * @returns 当前素材类型的接受文件类型
 */
export const getAcceptFileTypes = (currentMaterialType: string, acceptFileTypes: string) => {
  if (currentMaterialType === 'image') {
    return '.bmp,.png,.jpeg,.jpg,.gif';
  }
  if (currentMaterialType === 'voice') {
    return '.mp3,.wma,.wav,.amr';
  }
  if (currentMaterialType === 'video') {
    return '.mp4';
  }
  return acceptFileTypes;
};

/**
 * 验证文件是否符合要求
 * @param file 文件
 * @param currentMaterialType 当前素材类型
 * @param acceptFileTypes 接受文件类型
 * @returns 是否符合要求
 */
export const validateFile = (
  file: File,
  currentMaterialType: string,
  acceptFileTypes: string,
): boolean => {
  // 检查文件类型
  const isImage = currentMaterialType === 'image';

  if (isImage) {
    // 检查图片格式
    const isValidImageType = IMAGE_TYPES.includes(file.type);
    if (!isValidImageType) {
      message.error('只支持 BMP、PNG、JPEG、JPG、GIF 格式的图片');
      return false;
    }

    // 检查图片大小
    const isValidSize = file.size <= IMAGE_MAX_SIZE;
    if (!isValidSize) {
      message.error('图片大小不能超过 10MB');
      return false;
    }
  } else if (currentMaterialType === 'voice') {
    const isValidVoiceType = VOICE_TYPES.includes(file.type.toLowerCase());
    if (!isValidVoiceType) {
      message.error('只支持 mp3/wma/wav/amr 格式的音频');
      return false;
    }

    // 检查音频大小
    const isValidSize = file.size <= VOICE_MAX_SIZE;
    if (!isValidSize) {
      message.error('音频大小不能超过 2M');
      return false;
    }
  } else if (currentMaterialType === 'video') {
    // 检查视频格式
    const isValidVideoType = VIDEO_TYPES.includes(file.type);
    if (!isValidVideoType) {
      message.error('只支持 mp4 格式的视频');
      return false;
    }

    // 检查视频大小
    const isValidSize = file.size <= VIDEO_MAX_SIZE;
    if (!isValidSize) {
      message.error('视频大小不能超过 10MB');
      return false;
    }
  } else {
    // 其他类型素材的检查
    const isValidType = file.type.startsWith(acceptFileTypes.replace('*', ''));
    if (!isValidType) {
      message.error(`请上传${acceptFileTypes}类型的文件`);
      return false;
    }
  }

  return true;
};

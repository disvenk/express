package com.xxx.user.service;

import com.aliyun.oss.model.PutObjectResult;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.UploadFile;
import com.xxx.utils.OSSClientUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 上传图片
 * @author Chen.zm
 * @create 2017-03-22 14:31
 */
@Service
public class UploadFileService extends CommonService {

    /**
     * 上传图片 返回图片Id
     * @param base64
     * @return
     * @throws ResponseEntityException
     * @throws UpsertException
     */
    public Integer saveOssUploadFileByBase64(String base64) throws ResponseEntityException, UpsertException {
        UploadFile uploadFile = saveOssUploadFile();
        try {
            //上传图片
            OSSClientUtil.putBase64Img(uploadFile.getId() + "", base64, "jpg");
        } catch (Exception e) {
            deleteUploadFile(uploadFile);
            OSSClientUtil.deleteObject(uploadFile.getId() + "");
            throw new ResponseEntityException(400, "图片上传失败");
        }
        return uploadFile.getId();
    }

    /**
     * 上传视频 返回视频Id
     * @param base64
     * @return
     * @throws ResponseEntityException
     * @throws UpsertException
     */
    public Integer saveOssUploadFileByBase64_video(String base64) throws ResponseEntityException, UpsertException {
        UploadFile uploadFile = saveOssUploadFile_video();
        try {
            //上传
           OSSClientUtil.putBase64Img(uploadFile.getId() + "", base64, "mp4");
        } catch (Exception e) {
            deleteUploadFile(uploadFile);
            OSSClientUtil.deleteObject(uploadFile.getId() + "");
            throw new ResponseEntityException(400, "视频上传失败");
        }
        return uploadFile.getId();
    }


    /**
     * 根据图片路径上传图片 返回图片Id
     * @param url
     * @return
     * @throws ResponseEntityException
     * @throws UpsertException
     */
    public Integer saveOssUploadFileByUrl(String url) throws ResponseEntityException, UpsertException {
        UploadFile uploadFile = saveOssUploadFile();
        try {
            //上传图片
            OSSClientUtil.putObjectUrl(uploadFile.getId() + "", url, "jpg");
        } catch (Exception e) {
            deleteUploadFile(uploadFile);
            OSSClientUtil.deleteObject(uploadFile.getId() + "");
            throw new ResponseEntityException(400, "图片上传失败");
        }
        return uploadFile.getId();
    }

    /**
     * 根据图片字节上传图片 返回图片Id
     * @param bytes
     * @return
     * @throws ResponseEntityException
     * @throws UpsertException
     */
    public Integer saveOssUploadFileByByte(byte[] bytes, String fileSuffix) throws ResponseEntityException, UpsertException {
        UploadFile uploadFile = saveOssUploadFile();
        try {
            //上传图片
            OSSClientUtil.putObject(uploadFile.getId() + "", bytes, fileSuffix);
        } catch (Exception e) {
            deleteUploadFile(uploadFile);
            OSSClientUtil.deleteObject(uploadFile.getId() + "");
            throw new ResponseEntityException(400, "图片上传失败");
        }
        return uploadFile.getId();
    }

    /**
     * 根据视频字节上传视频  返回视频Id
     * @param bytes
     * @return
     * @throws ResponseEntityException
     * @throws UpsertException
     */
    public Integer saveOssUploadFileByByte_video(byte[] bytes, String fileSuffix) throws ResponseEntityException, UpsertException {
        UploadFile uploadFile = saveOssUploadFile_video();
        try {
            OSSClientUtil.putObject(uploadFile.getId() + "", bytes, fileSuffix);
        } catch (Exception e) {
            deleteUploadFile(uploadFile);
            OSSClientUtil.deleteObject(uploadFile.getId() + "");
            throw new ResponseEntityException(400, "视频上传失败");
        }
        return uploadFile.getId();
    }

    /**
     * 创建图片对象
     * @return
     */
    @CacheEvict(value={"UploadFile"}, allEntries = true)
    public UploadFile saveOssUploadFile() throws UpsertException {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setUploadType(1);
        uploadFile.setFileType(1);
        return upsert2(uploadFile);
    }

    /**
     * 创建视频对象
     * @return
     */
    @CacheEvict(value={"UploadFile"}, allEntries = true)
    public UploadFile saveOssUploadFile_video() throws UpsertException {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setUploadType(1);
        uploadFile.setFileType(2);
        return upsert2(uploadFile);
    }


    /**
     * 删除图片对象
     * @param uploadFile
     */
    @CacheEvict(value={"UploadFile"}, allEntries = true)
    public void deleteUploadFile(UploadFile uploadFile) throws UpsertException {
        delete(uploadFile);
        OSSClientUtil.deleteObject(uploadFile.getId() + "");
    }

    @CacheEvict(value={"UploadFile"}, allEntries = true)
    public void deleteUploadFile(Integer id) throws UpsertException {
        UploadFile uploadFile = get2(UploadFile.class, id);
        if (uploadFile == null) return;
        delete(uploadFile);
        OSSClientUtil.deleteObject(uploadFile.getId() + "");
    }
}

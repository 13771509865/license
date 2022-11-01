package com.yozosoft.license.model.bo;

import lombok.Data;

/**
 * @author zhouf
 * 文档平台特异化授权部分
 */
@Data
public class DocLicenseBO extends BaseLicenseBO{

    private Boolean platform;
    /**
     * 租户数量
     */
    private Integer tenant_count;

    private Boolean preview_office_sd;

    private Boolean preview_office_hd;

    private Boolean preview_office_safe;

    private Boolean preview_pdf_sd;

    private Boolean preview_pdf_hd;

    private Boolean preview_ofd;

    private Boolean preview_zip;

    private Boolean preview_pic;

    private Boolean preview_text;

    private Boolean preview_cad_standard;

    private Boolean preview_cad_profession;

    private Boolean preview_psd;

    private Boolean preview_ai;

    private Boolean preview_indd;

    private Boolean preview_cdr;

    private Boolean preview_epub;

    private Boolean preview_3d;

    private Boolean preview_with_mark;

    private Boolean preview_with_revise;

    private Boolean preview_with_note;

    private Boolean preview_with_sign;

    private Boolean convert_to_pdf;

    private Boolean convert_document_to_ofd;

    private Boolean convert_pic_to_ofd;

    private Boolean convert_to_pic;

    private Boolean convert_to_mp4;

    private Boolean convert_to_mp3;

    private Boolean merge_office;

    private Boolean split_office;

    private Boolean merge_pdf;

    private Boolean split_pdf;

    private Boolean pick_office;

    private Boolean fill_office;

    private Boolean edit_document;
}

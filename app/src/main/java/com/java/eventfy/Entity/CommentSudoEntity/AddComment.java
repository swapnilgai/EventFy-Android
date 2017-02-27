package com.java.eventfy.Entity.CommentSudoEntity;

import com.java.eventfy.Entity.Comments;

/**
 * Created by swapnil on 2/4/17.
 */

public class AddComment {

    private Comments comment;

    public byte[] getBitmapByteArray() {
        return bitmapByteArray;
    }

    public void setBitmapByteArray(byte[] bitmapByteArray) {
        this.bitmapByteArray = bitmapByteArray;
    }

    private  byte[] bitmapByteArray;

    public String getViewMsg() {
        return viewMsg;
    }

    public void setViewMsg(String viewMsg) {
        this.viewMsg = viewMsg;
    }

    private String viewMsg;

    public Comments getComment() {
        return comment;
    }

    public void setComment(Comments comment) {
        this.comment = comment;
    }



}

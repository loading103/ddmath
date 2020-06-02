///*
// * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
// * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
// * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
// * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
// * Vestibulum commodo. Ut rhoncus gravida arcu.
// */
//
//package com.tsinghuabigdata.edu.ddmath.commons;
//
//import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
//import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
//import com.tsinghuabigdata.edu.ddmath.util.AsyncTaskCancel;
//
///**
// * 验证码操作
// * Created by yanshen on 2016/3/25.
// */
//public class ValidateCodeOperation {
//
//    private ValidateCodeService mValidateCodeService;
//    private SendValidateCodeTask mSendValidateCodeTask;
//    private CheckValidateCodeTask mCheckValidateCodeTask;
//
//    private CodeListener mCodeListener = new DefalutCodeListener();
//
//    public ValidateCodeOperation(ValidateCodeService mValidateCodeService) {
//        this.mValidateCodeService = mValidateCodeService;
//    }
//
//    public void setCodeListener(CodeListener codeListener) {
//        this.mCodeListener = codeListener;
//    }
//
//    /**
//     * 发送验证码
//     *
//     * @param mobile
//     * @param codeType
//     */
//    public void sendCode(String mobile, String codeType) {
//        AsyncTaskCancel.cancel(mSendValidateCodeTask);
//        mSendValidateCodeTask = new SendValidateCodeTask();
//        mSendValidateCodeTask.execute(mobile, codeType);
//    }
//
//    /**
//     * 效验验证码
//     *
//     * @param mobile
//     * @param codeType
//     * @param code
//     */
//    public void checkCode(String mobile, String codeType, String code) {
//        AsyncTaskCancel.cancel(mCheckValidateCodeTask);
//        mCheckValidateCodeTask = new CheckValidateCodeTask();
//        mCheckValidateCodeTask.execute(mobile, codeType, code);
//    }
//
//    /**
//     * 销毁
//     */
//    public void destroy() {
//        // cancel request task
//        AsyncTaskCancel.cancel(mSendValidateCodeTask);
//        AsyncTaskCancel.cancel(mCheckValidateCodeTask);
//        mSendValidateCodeTask = null;
//        mCheckValidateCodeTask = null;
//    }
//
//    /**
//     * 发送验证码
//     */
//    class SendValidateCodeTask extends AppAsyncTask<String, Void, Boolean> {
//
//        @Override
//        protected Boolean doExecute(String... params) throws Exception {
//            String mobile = params[0];
//            return mValidateCodeService.sendValidateCode(mobile, "app", params[1]);
//        }
//
//        @Override
//        protected void onResult(Boolean res) {
//            if (res) {
//                mCodeListener.onSendSuccess();
//            } else {
//                mCodeListener.onSendFault(new Exception("result is false"));
//            }
//        }
//
//        @Override
//        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
//            mCodeListener.onSendFault(ex);
//        }
//    }
//
//    class CheckValidateCodeTask extends AppAsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doExecute(String... params) throws Exception {
//            String mobile = params[0];
//            String codeType = params[1];
//            String code = params[2];
//            mValidateCodeService.checkValidateCode(mobile, "app", codeType, code);
//            return null;
//        }
//
//        @Override
//        protected void onResult(Void aVoid) {
//            mCodeListener.onValidateSuccess();
//        }
//
//        @Override
//        protected void onFailure(HttpResponse<Void> response, Exception ex) {
//            mCodeListener.onValidateFault(ex);
//        }
//    }
//
//    public interface CodeListener {
//
//        void onSendSuccess();
//
//        void onSendFault(Exception ex);
//
//        void onValidateSuccess();
//
//        void onValidateFault(Exception ex);
//    }
//
//    class DefalutCodeListener implements CodeListener {
//
//        @Override
//        public void onSendSuccess() {
//
//        }
//
//        @Override
//        public void onSendFault(Exception ex) {
//
//        }
//
//        @Override
//        public void onValidateSuccess() {
//
//        }
//
//        @Override
//        public void onValidateFault(Exception ex) {
//
//        }
//    }
//}

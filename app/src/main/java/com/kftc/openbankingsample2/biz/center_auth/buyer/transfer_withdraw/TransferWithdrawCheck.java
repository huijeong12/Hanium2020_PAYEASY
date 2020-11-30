package com.kftc.openbankingsample2.biz.center_auth.buyer.transfer_withdraw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.http.CenterAuthApiRetrofitAdapter;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.biz.main.HomeFragment;
import com.kftc.openbankingsample2.common.data.ApiCallAccountTransactionResponse;
import com.kftc.openbankingsample2.common.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TransferWithdrawCheck extends AbstractCenterAuthMainFragment {
    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;
    private String[] info;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();

        info = args.getStringArray("key");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transfer_withdraw_check, container, false);
        initView();
        return view;
    }

    void initView() {

        // 가게 이름
        EditText etRecvMarketName = view.findViewById(R.id.recv_client_market_name);
        etRecvMarketName.setText(info[0]);

        // 수취인 이름
        EditText etRecvClntName = view.findViewById(R.id.recv_client_name);
        etRecvClntName.setText(info[9]);

        // 거래 금액
        EditText etTranAmt = view.findViewById(R.id.trans_amt);
        etTranAmt.setText(info[5]);

        // 수취인 계좌번호
        EditText etRecvClientAccountNum = view.findViewById(R.id.recv_client_account_num);
        etRecvClientAccountNum.setText(info[11]);

        // 출금이체 요청
        view.findViewById(R.id.btnNext).setOnClickListener(v -> {

            // 직전내용 저장
            String accessToken = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_ACCESS_TOKEN);
            Utils.saveData(CenterAuthConst.CENTER_AUTH_ACCESS_TOKEN, accessToken);
            String cntrAccountNum = info[2];
            Utils.saveData(CenterAuthConst.CENTER_AUTH_CNTR_ACCOUNT_NUM, cntrAccountNum);
            String fintechUseNum = info[4];
            Utils.saveData(CenterAuthConst.CENTER_AUTH_FINTECH_USE_NUM, fintechUseNum);
            String reqClientName = info[6];
            Utils.saveData(CenterAuthConst.CENTER_AUTH_REQ_CLIENT_NAME, reqClientName);

            // 은행거래고유번호(20자리)
            // 하루동안 유일성이 보장되어야함. 이용기관번호(10자리) + 생성주체구분코드(1자리, U:이용기관, O:오픈뱅킹) + 이용기관 부여번호(9자리)
            String clientUseCode = "T991636280";
            String randomUnique9String = Utils.getCurrentTime();    // 이용기관 부여번호를 임시로 시간데이터 사용
            String bankTranId = String.format("%sU%s", clientUseCode, randomUnique9String);

            // 요청전문
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("bank_tran_id", bankTranId);
            paramMap.put("cntr_account_type", info[0]);
            paramMap.put("cntr_account_num", info[1]);
            paramMap.put("dps_print_content", info[2]);
            paramMap.put("fintech_use_num", info[3]);
            paramMap.put("tran_amt", info[4]);
            paramMap.put("tran_dtime", new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date()));
            paramMap.put("req_client_name", info[5]);
            paramMap.put("req_client_fintech_use_num", info[6]);
            paramMap.put("req_client_num", info[7]);
            paramMap.put("transfer_purpose", info[8]);
            paramMap.put("recv_client_name", info[9]);
            paramMap.put("recv_client_bank_code", info[10]);
            paramMap.put("recv_client_account_num", info[11]);

            showProgress();

            // Retrofit 오픈소스 이용하여 출금이체 API 호출
            CenterAuthApiRetrofitAdapter.getInstance()
                    .transferWithdrawFinNum("Bearer " + accessToken, paramMap)
                    .enqueue(super.handleResponse("tran_amt", "이체완료!! 이체금액", responseJson -> {

                                String BankTranId = setRandomBankTranIdCustom();
                                String inquiryType = "A";
                                String inquiryBase = "D";
                                String fromData = "20180101";
                                String fromTime = "";
                                String toDate = Utils.getDateString8(0);
                                String toTime = "";
                                String sortOrder = "D";
                                String tranDtime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date());
                                String beforeInquiryTraceInfo = "123";

                                // 요청전문
                                HashMap<String, String> paramMap2 = new HashMap<>();
                                paramMap2.put("bank_tran_id", BankTranId);
                                paramMap2.put("fintech_use_num", fintechUseNum);
                                paramMap2.put("inquiry_type", inquiryType);
                                paramMap2.put("inquiry_base", inquiryBase);
                                paramMap2.put("from_date", fromData);
                                paramMap2.put("from_time", fromTime);
                                paramMap2.put("to_date", toDate);
                                paramMap2.put("to_time", toTime);
                                paramMap2.put("sort_order", sortOrder);
                                paramMap2.put("tran_dtime", tranDtime);
                                paramMap2.put("befor_inquiry_trace_info", beforeInquiryTraceInfo);

                                showProgress();

                                // Retrofit 이용하여 거래 내역 조회 API 호출
                                CenterAuthApiRetrofitAdapter.getInstance()
                                        .accountTrasactionListFinNum("Bearer " + accessToken, paramMap2)
                                        .enqueue(super.handleResponse("page_record_cnt", "현재페이지 조회건수", responseJson2 -> {

                                                    // 성공하면 결과화면으로 이동
                                                    ApiCallAccountTransactionResponse result =
                                                            new Gson().fromJson(responseJson2, ApiCallAccountTransactionResponse.class);
                                                    args.putParcelable("result", result);
                                                    args.putSerializable("request", paramMap2);
                                                    args.putString(CenterAuthConst.BUNDLE_KEY_ACCESS_TOKEN, accessToken);

                                                    // 거래 내역 조회 창으로 이동
                                                    startFragment(HomeFragment.class, args, R.string.fragment_id_api_call_transaction);
                                                })
                                        );
                            })
                    );

        });

        // 취소
        view.findViewById(R.id.btnRescan).setOnClickListener(v -> onBackPressed());
    }
}

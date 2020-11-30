package com.kftc.openbankingsample2.biz.center_auth.buyer.transfer_withdraw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
            /*
                * String[] strings = { QRInfo[2], buyerInfo[0], buyerInfo[1], QRInfo[1], buyerInfo[2],
                        QRInfo[0], buyerInfo[3], buyerInfo[4], buyerInfo[5],
                        QRInfo[3], QRInfo[4], QRInfo[5]};
                * String[] strings = { item.getAccount_type(), item.getAccount_num(), item.getFintech_use_num(),
                                    item.getAccount_holder_name(), item.getFintech_use_num(), item.getPayer_num(), "TR"};*
                * String data = total_price + " Pay:easy-입금 한이음분식 김오픈 097 232000067812";
                * info = { 가게명, cntr_account_type, cntr_account_num, dps_print_content, fintech_use_num, tran_amt,
                *           req_client_name, req_client_fintech_use_num, req_client_num, transfer_purpose,
                *           recv_client_name, recv_bank_code, recv_client_account_numt
             */

            // 직전내용 저장
            String accessToken = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_ACCESS_TOKEN);
            Utils.saveData(CenterAuthConst.CENTER_AUTH_ACCESS_TOKEN, accessToken);
            String cntrAccountNum = info[2];
            Utils.saveData(CenterAuthConst.CENTER_AUTH_CNTR_ACCOUNT_NUM, cntrAccountNum);
            String fintechUseNum = info[4];
            Utils.saveData(CenterAuthConst.CENTER_AUTH_FINTECH_USE_NUM, fintechUseNum);
            String reqClientName = info[6];
            Utils.saveData(CenterAuthConst.CENTER_AUTH_REQ_CLIENT_NAME, reqClientName);
            String withdrawAcc = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CONTRACT_WITHDRAW_ACCOUNT_NUM);
            Utils.saveData(CenterAuthConst.CENTER_AUTH_CONTRACT_WITHDRAW_ACCOUNT_NUM, withdrawAcc);
            withdrawAcc = "8487279403";

            // 은행거래고유번호(20자리)
            // 하루동안 유일성이 보장되어야함. 이용기관번호(10자리) + 생성주체구분코드(1자리, U:이용기관, O:오픈뱅킹) + 이용기관 부여번호(9자리)
            String clientUseCode = "T991636280";
            String randomUnique9String = Utils.getCurrentTime();    // 이용기관 부여번호를 임시로 시간데이터 사용
            String bankTranId = String.format("%sU%s", clientUseCode, randomUnique9String);

            for (int i = 1; i < 12; i++)
                Log.d("pay-easy", info[i] + "\n");

            // 요청전문
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("bank_tran_id", bankTranId);
            paramMap.put("cntr_account_type", info[1]);
            paramMap.put("cntr_account_num", withdrawAcc);
            paramMap.put("dps_print_content", info[0]);
            paramMap.put("fintech_use_num", info[4]);
            paramMap.put("tran_amt", info[5]);
            paramMap.put("tran_dtime", new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date()));
            paramMap.put("req_client_name", info[6]);
            paramMap.put("req_client_fintech_use_num", info[4]);
            paramMap.put("req_client_num", clientUseCode);
            paramMap.put("transfer_purpose", "TR");
            paramMap.put("recv_client_name", info[9]);
            paramMap.put("recv_client_bank_code", info[10]);
            paramMap.put("recv_client_account_num", info[11]);

            showProgress();

            // Retrofit 오픈소스 이용하여 출금이체 API 호출
            CenterAuthApiRetrofitAdapter.getInstance()
                    .transferWithdrawFinNum("Bearer " + accessToken, paramMap)
                    .enqueue(super.handleResponse("tran_amt", "이체완료!! 이체금액", responseJson -> {
                                Bundle sendingArgs = new Bundle();
                                /*
                                * 가게명, 받는 사람, 수취 계좌, 거래 금액, 보낸 사람 핀테크 이용 번호
                                 */
                                String[] transferResultInfo = { info[0], info[9], info[11], info[5], info[4] };
                                Bundle bundle = new Bundle();
                                bundle.putStringArray("trans_info", transferResultInfo);
                                startFragment(TransferWithdrawResult.class, bundle, R.string.fragment_id_transfer_withdraw_result);
                            })
                    );

        });

        // 취소
        view.findViewById(R.id.btnRescan).setOnClickListener(v -> onBackPressed());
    }
}

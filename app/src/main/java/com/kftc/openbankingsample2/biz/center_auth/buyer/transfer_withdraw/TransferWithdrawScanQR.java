package com.kftc.openbankingsample2.biz.center_auth.buyer.transfer_withdraw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.buyer.account_list.AccountListFragment;

public class TransferWithdrawScanQR extends AbstractCenterAuthMainFragment {

    // context
    Context context;

    // data
    Bundle args;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();

        scanQR();
    }

    public void scanQR() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this.activity);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setPrompt("QR코드가 중앙에 오면 인식이 시작됩니다.");
        intentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // QR코드 인식 결과
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            String resultContents = result.getContents();

            if (result.getContents() != null) {

                String[] QRInfo = resultContents.split(" ");
                String[] buyerInfo = args.getStringArray("buyer_info");

                /*
                가게명, 계좌 유형, 요청인 계좌번호, 인자내역, 요청인 핀테크번호, 거래 금액,
                요청인 이름, 요청인 핀테크 번호, 요청인 클라이언트 번호,
                출금 목적, 수취인 이름, 수취인 은행 코드, 수취인 계좌번호
                 */

                String[] strings = { QRInfo[2], buyerInfo[0], buyerInfo[1], QRInfo[1], buyerInfo[2],
                        QRInfo[0], buyerInfo[3], buyerInfo[4], buyerInfo[5],
                        QRInfo[3], QRInfo[4], QRInfo[5]};

                // 결제 정보 확인 창에 보낼 데이터
                Bundle sendingArgs = new Bundle();
                sendingArgs.putStringArray("key", strings);

                // 결제 정보 확인 창으로 이동
                startFragment(TransferWithdrawCheck.class, sendingArgs, R.string.fragment_id_transfer_withdraw_check);
            }

            else {

            }
        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

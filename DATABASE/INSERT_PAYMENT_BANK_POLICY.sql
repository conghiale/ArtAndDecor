-- Insert payment bank information policy for QR code generation
-- This policy contains bank configuration for generating payment QR codes

INSERT INTO POLICY (
    POLICY_NAME, 
    POLICY_SLUG, 
    POLICY_VALUE, 
    POLICY_DISPLAY_NAME, 
    POLICY_REMARK,
    POLICY_ENABLED,
    CREATED_DT,
    MODIFIED_DT
) VALUES (
    'PAYMENT_BANK_INFO',
    'payment-bank-info',
    'bank.name=Vietcombank
bank.account.name=NGUYEN VAN A
bank.account.number=1017231078
bank.branch=Chi nhanh Nam Sai Gon
bank.bin=970436
bank.qr.code=https://img.vietqr.io/image/970436-1017231078-compact.png
bank.note.template=Thanh toan don hang {orderId}',
    'Thông Tin Ngân Hàng Thanh Toán',
    'Configuration for payment bank information used in QR code generation. Contains bank details, account info, and payment templates.',
    true,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    POLICY_VALUE = 'bank.name=Vietcombank
bank.account.name=NGUYEN VAN A
bank.account.number=1017231078
bank.branch=Chi nhanh Nam Sai Gon
bank.bin=970436
bank.qr.code=https://img.vietqr.io/image/970436-1017231078-compact.png
bank.note.template=Thanh toan don hang {orderId}',
    POLICY_DISPLAY_NAME = 'Thông Tin Ngân Hàng Thanh Toán',
    POLICY_REMARK = 'Configuration for payment bank information used in QR code generation. Contains bank details, account info, and payment templates.',
    POLICY_ENABLED = true,
    MODIFIED_DT = NOW();
package pe.com.scotiabank.blpm.android.client.host.session.subflow

import pe.com.scotiabank.blpm.android.client.nosession.login.factor.SuccessfulAuth

class SuccessfulAuthToLaunch(val successfulAuth: SuccessfulAuth, val launcher: SubFlowLauncher)
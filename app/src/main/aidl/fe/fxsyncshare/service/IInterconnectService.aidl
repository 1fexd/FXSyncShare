package fe.fxsyncshare.service;

import fe.fxsyncshare.service.IContextActionsCallback;

interface IInterconnectService {
    void provideContextActions(String url, in IContextActionsCallback callback) = 1;
}

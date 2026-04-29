package com.kmaebashi.nctfw;

public interface ControllerInvoker {
    RoutingResult invoke(ThrowableFunction<RequestContext, RoutingResult> logic);
    RoutingResult invokeApi(ThrowableFunction<RequestContext, RoutingResult> logic);
}

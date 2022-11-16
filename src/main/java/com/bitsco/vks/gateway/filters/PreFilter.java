package com.bitsco.vks.gateway.filters;

import com.bitsco.vks.common.constant.Constant;
import com.bitsco.vks.common.response.Response;
import com.bitsco.vks.common.response.ResponseBody;
import com.bitsco.vks.common.response.Token;
import com.bitsco.vks.common.util.StringCommon;
import com.bitsco.vks.gateway.cache.CacheService;
import com.bitsco.vks.gateway.model.Role;
import com.bitsco.vks.gateway.model.User;
import com.bitsco.vks.gateway.model.UserRole;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by IntelliJ IDEA.
 * User: Truong Nguyen
 * Date: 17-Dec-18
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class PreFilter extends ZuulFilter {
    private static final Logger LOGGER = LogManager.getLogger("DB");

    @Autowired
    CacheService cacheService;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();
        LOGGER.info("--------------------USERNAME-----------------------------: " + request.getHeader(Constant.KEY.USERNAME));

        if (request.getMethod().equals(RequestMethod.OPTIONS.name()))
            return null;
        String uri = request.getRequestURI();
        if (uri.toLowerCase().contains("/ping")
                || uri.toLowerCase().contains("/api/sso/token/check")
                || uri.toLowerCase().contains("/api/sso/auth/login")
                || uri.toLowerCase().contains("/api/sso/auth/signup")
                || uri.trim().contains("/api/sso/password/resetPasswordByUser")
                || uri.toLowerCase().contains("/api/sso/verify")
                || uri.toLowerCase().contains("api/quanlyan/dm/admgrant/getmenubymodule")
        )
            return null;
        String accessToken = getJwtFromRequest(request);
        LOGGER.info("accessToken: " + accessToken);
        if (StringCommon.isNullOrBlank(accessToken)) {
            setFailedRequest((new ResponseBody(Response.MISSING_PARAM.getResponseCode(), "Token không được để trống")).toString());
            return null;
        }
        Token token = cacheService.getTokenFromCache(accessToken);
        LOGGER.info("token::::::::::::::::::::::::::::::::::: " + token);
        if (token == null) {
            if (!uri.toLowerCase().contains("api/quanlyan")) {
                setFailedRequest((new ResponseBody(Response.TOKEN_NOT_FOUND)).toString());
            }
            return null;
        } else {
            ctx.addZuulRequestHeader(Constant.KEY.USERNAME, token.getUsername());
            response.setHeader(Constant.KEY.USERNAME, token.getUsername());
        }
        if (!uri.toLowerCase().contains("api/quanlyan")) {
            User user = cacheService.getUserFromCache(token.getUsername());
            if (user == null) {
                setFailedRequest((new ResponseBody(Response.DATA_INVALID.getResponseCode(), "Tài khoản không tồn tại")).toString());
                return null;
            } else if (user.getStatus() == null) {
                setFailedRequest((new ResponseBody(Response.DATA_INVALID.getResponseCode(), "Không xác định được trạng thái tài khoản")).toString());
                return null;
            } else if (user.getStatus() == Constant.STATUS_OBJECT.INACTIVE) {
                setFailedRequest((new ResponseBody(Response.DATA_INVALID.getResponseCode(), "Tài khoản không hoạt động")).toString());
                return null;
            }

            //Kiểm tra user có quyền thực thi API đó không(nếu API được khai báo trong danh sách API phương thức/method bị kiểm soát?
            ResponseBody responseBody = checkUserRole(user.getId(), uri);
            if (!responseBody.getResponseCode().equals(Response.SUCCESS.getResponseCode())) {
                setFailedRequest((responseBody).toString());
                return null;
            }

            ctx.addZuulRequestHeader(Constant.KEY.USERNAME, user.getUsername());
            response.setHeader(Constant.KEY.USERNAME, user.getUsername());
        }
        return null;
    }

    /**
     * Reports an error message given a response body and code.
     *
     * @param body
     */
    private void setFailedRequest(String body) {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        ctx.setResponseStatusCode(HttpStatus.OK.value());
        if (ctx.getResponseBody() == null) {
            response.setContentType(Constant.CONTENT_TYPE_APPLICATION_JSON_UTF8);
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(false);
        }
        LOGGER.info("ctx: " + ctx);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        LOGGER.info("bearerToken: " + bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private ResponseBody checkUserRole(long userId, String uri) {
        if (!uri.contains("/api/"))
            return new ResponseBody(Response.DATA_INVALID, "Uri không đúng định dạng. Không chứa /api/");
        uri = uri.substring(uri.indexOf("/api/"));
        //Lấy role phương thức(các method thêm mới/sửa/xóa ...) nếu có khai báo validate role
        Role role = cacheService.getRoleFromCache(uri);
        if (role == null)
            return new ResponseBody(Response.SUCCESS);
        //nếu method đó được khai báo(tồn tại) kiểm tra user đó có quyền role đó không
        UserRole userRole = cacheService.getUserRoleFromCache(userId, role.getId());
        if (userRole != null && userRole.getStatus() != null && userRole.getStatus() == Constant.STATUS_OBJECT.ACTIVE)
            return new ResponseBody(Response.SUCCESS);
        else return new ResponseBody(Response.UNAUTHORIZED);
    }
}

package io.github.azirzsk.filedownloadhub.utils;

import io.github.azirzsk.filedownloadhub.entity.RangeHeader;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author azir
 * @since 2025/04/11
 */
@Slf4j
public class HeaderUtils {

    private static final Pattern RANGE_PATTERN = Pattern.compile("^bytes=\\d+(-\\d*)?$");

    public static RangeHeader getRangeHeader(HttpServletRequest request) {
        String range = request.getHeader(HttpHeaders.RANGE);
        if (!StringUtils.hasText(range)) {
            return null;
        }
        Matcher matcher = RANGE_PATTERN.matcher(range);
        if (!matcher.matches()) {
            log.warn("无效的range请求头");
            throw new IllegalArgumentException("Invalid range header: " + range);
        }
        String realRange = range.substring(6);
        String[] splitRange = realRange.split("-");
        RangeHeader res = new RangeHeader();
        if (splitRange.length == 2) {
            long start = Long.parseLong(splitRange[0]);
            long end = Long.parseLong(splitRange[1]);
            if (start > end) {
                log.warn("读取的字节不合法：{}", range);
                throw new IllegalArgumentException("Invalid range header: " + range);
            }
            res.setStart(start);
            res.setEnd(end);
        } else if (splitRange.length == 1) {
            res.setStart(Long.parseLong(splitRange[0]));
        }
        if (log.isDebugEnabled()) {
            log.debug("header-range:{}", res);
        }
        return res;
    }

}

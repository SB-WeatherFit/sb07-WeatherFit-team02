# Nginx 로드 밸런서 & 리버스 프록시용 Dockerfile
FROM nginx:alpine

# 작업 디렉터리 설정
WORKDIR /etc/nginx

# 기본 설정 파일 제거
RUN rm /etc/nginx/conf.d/default.conf

# 커스텀 설정 파일 복사
COPY conf/default.conf /etc/nginx/conf.d/

# 캐시 디렉터리 생성
RUN mkdir -p /var/cache/nginx && \
    chown -R nginx:nginx /var/cache/nginx

# 로그 디렉터리 권한 설정
RUN touch /var/log/nginx/access.log /var/log/nginx/error.log && \
    chown nginx:nginx /var/log/nginx/access.log /var/log/nginx/error.log

# Nginx 설정 문법 검사 (빌드 시점에서는 upstream이 없어서 주석 처리)
# RUN nginx -t

# 포트 노출
EXPOSE 80

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost/nginx_status || exit 1

# Nginx 실행 (포그라운드)
CMD ["nginx", "-g", "daemon off;"]

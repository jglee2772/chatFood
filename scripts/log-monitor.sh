#!/bin/bash

# ChatFood 로그 모니터링 스크립트
# 실시간으로 로그를 모니터링하고 알림을 제공합니다.

LOG_DIR="logs"
LOG_FILE="$LOG_DIR/chatfood.log"
ERROR_LOG="$LOG_DIR/chatfood-error.log"
JSON_LOG="$LOG_DIR/chatfood-json.log"

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔍 ChatFood 로그 모니터링 시작${NC}"
echo "=========================================="

# 로그 디렉토리 생성
mkdir -p "$LOG_DIR"

# 실시간 로그 모니터링 함수
monitor_logs() {
    echo -e "${GREEN}📊 실시간 로그 모니터링 중...${NC}"
    echo "Ctrl+C로 종료할 수 있습니다."
    echo ""
    
    # tail -f로 실시간 로그 모니터링
    tail -f "$LOG_FILE" 2>/dev/null | while read line; do
        timestamp=$(date '+%H:%M:%S')
        
        # 로그 레벨에 따른 색상 적용
        if echo "$line" | grep -q " ERROR "; then
            echo -e "${RED}[$timestamp] $line${NC}"
        elif echo "$line" | grep -q " WARN "; then
            echo -e "${YELLOW}[$timestamp] $line${NC}"
        elif echo "$line" | grep -q " INFO "; then
            echo -e "${GREEN}[$timestamp] $line${NC}"
        else
            echo -e "${BLUE}[$timestamp] $line${NC}"
        fi
    done
}

# 로그 통계 함수
show_stats() {
    echo -e "${BLUE}📈 로그 통계${NC}"
    echo "============="
    
    if [ -f "$LOG_FILE" ]; then
        total_lines=$(wc -l < "$LOG_FILE")
        error_count=$(grep -c " ERROR " "$LOG_FILE" 2>/dev/null || echo "0")
        warn_count=$(grep -c " WARN " "$LOG_FILE" 2>/dev/null || echo "0")
        info_count=$(grep -c " INFO " "$LOG_FILE" 2>/dev/null || echo "0")
        
        echo "총 로그 수: $total_lines"
        echo "ERROR: $error_count"
        echo "WARN: $warn_count"
        echo "INFO: $info_count"
        
        if [ "$total_lines" -gt 0 ]; then
            error_rate=$((error_count * 100 / total_lines))
            echo "에러율: ${error_rate}%"
        fi
    else
        echo "로그 파일이 없습니다."
    fi
}

# 최근 에러 확인 함수
check_recent_errors() {
    echo -e "${RED}🚨 최근 에러 확인${NC}"
    echo "=================="
    
    if [ -f "$ERROR_LOG" ]; then
        echo "최근 10개 에러:"
        tail -10 "$ERROR_LOG"
    else
        echo "에러 로그 파일이 없습니다."
    fi
}

# 로그 파일 크기 확인 함수
check_log_sizes() {
    echo -e "${YELLOW}📏 로그 파일 크기${NC}"
    echo "==============="
    
    for log_file in "$LOG_FILE" "$ERROR_LOG" "$JSON_LOG"; do
        if [ -f "$log_file" ]; then
            size=$(du -h "$log_file" | cut -f1)
            echo "$(basename "$log_file"): $size"
        fi
    done
}

# 메인 메뉴
show_menu() {
    echo ""
    echo -e "${BLUE}ChatFood 로그 모니터링 메뉴${NC}"
    echo "=============================="
    echo "1) 실시간 로그 모니터링"
    echo "2) 로그 통계 보기"
    echo "3) 최근 에러 확인"
    echo "4) 로그 파일 크기 확인"
    echo "5) 로그 분석 (Python 스크립트)"
    echo "6) 로그 파일 정리"
    echo "0) 종료"
    echo ""
    read -p "선택하세요 (0-6): " choice
    
    case $choice in
        1)
            monitor_logs
            ;;
        2)
            show_stats
            show_menu
            ;;
        3)
            check_recent_errors
            show_menu
            ;;
        4)
            check_log_sizes
            show_menu
            ;;
        5)
            if [ -f "scripts/log-analyzer.py" ]; then
                python3 scripts/log-analyzer.py "$LOG_FILE"
            else
                echo "로그 분석 스크립트를 찾을 수 없습니다."
            fi
            show_menu
            ;;
        6)
            echo "로그 파일을 정리하시겠습니까? (y/N)"
            read -p "> " confirm
            if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
                find "$LOG_DIR" -name "*.log.*" -mtime +7 -delete
                echo "7일 이상 된 로그 파일이 정리되었습니다."
            fi
            show_menu
            ;;
        0)
            echo "모니터링을 종료합니다."
            exit 0
            ;;
        *)
            echo "잘못된 선택입니다."
            show_menu
            ;;
    esac
}

# 스크립트 시작
if [ "$1" = "monitor" ]; then
    monitor_logs
elif [ "$1" = "stats" ]; then
    show_stats
elif [ "$1" = "errors" ]; then
    check_recent_errors
else
    show_menu
fi

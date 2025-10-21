#!/usr/bin/env python3
"""
ChatFood 로그 분석 스크립트
구조화된 로그를 분석하여 통계와 인사이트를 제공합니다.
"""

import json
import re
import sys
from datetime import datetime, timedelta
from collections import defaultdict, Counter
import argparse

class LogAnalyzer:
    def __init__(self, log_file_path):
        self.log_file_path = log_file_path
        self.stats = {
            'total_logs': 0,
            'error_count': 0,
            'warn_count': 0,
            'info_count': 0,
            'debug_count': 0,
            'api_calls': defaultdict(int),
            'user_actions': defaultdict(int),
            'error_patterns': Counter(),
            'response_times': [],
            'hourly_stats': defaultdict(int)
        }
    
    def analyze_logs(self):
        """로그 파일을 분석합니다."""
        print(f"📊 로그 분석 시작: {self.log_file_path}")
        
        try:
            with open(self.log_file_path, 'r', encoding='utf-8') as file:
                for line_num, line in enumerate(file, 1):
                    self._process_log_line(line, line_num)
            
            self._generate_report()
            
        except FileNotFoundError:
            print(f"❌ 로그 파일을 찾을 수 없습니다: {self.log_file_path}")
            sys.exit(1)
        except Exception as e:
            print(f"❌ 로그 분석 중 오류 발생: {e}")
            sys.exit(1)
    
    def _process_log_line(self, line, line_num):
        """개별 로그 라인을 처리합니다."""
        self.stats['total_logs'] += 1
        
        # 로그 레벨 분석
        if ' ERROR ' in line:
            self.stats['error_count'] += 1
            self._extract_error_pattern(line)
        elif ' WARN ' in line:
            self.stats['warn_count'] += 1
        elif ' INFO ' in line:
            self.stats['info_count'] += 1
        elif ' DEBUG ' in line:
            self.stats['debug_count'] += 1
        
        # API 호출 분석
        if 'API 호출' in line:
            api_match = re.search(r'API: (\w+)', line)
            if api_match:
                self.stats['api_calls'][api_match.group(1)] += 1
        
        # 사용자 액션 분석
        if '사용자 액션' in line:
            action_match = re.search(r'액션: (\w+)', line)
            if action_match:
                self.stats['user_actions'][action_match.group(1)] += 1
        
        # 응답 시간 분석
        response_time_match = re.search(r'소요시간: (\d+)ms', line)
        if response_time_match:
            self.stats['response_times'].append(int(response_time_match.group(1)))
        
        # 시간대 분석
        time_match = re.search(r'(\d{2}):\d{2}:\d{2}', line)
        if time_match:
            hour = int(time_match.group(1))
            self.stats['hourly_stats'][hour] += 1
    
    def _extract_error_pattern(self, line):
        """에러 패턴을 추출합니다."""
        # 일반적인 에러 패턴들
        error_patterns = [
            r'Exception: (\w+)',
            r'Error: (\w+)',
            r'Failed to (\w+)',
            r'Cannot (\w+)',
            r'Unable to (\w+)'
        ]
        
        for pattern in error_patterns:
            match = re.search(pattern, line)
            if match:
                self.stats['error_patterns'][match.group(1)] += 1
                break
    
    def _generate_report(self):
        """분석 결과 리포트를 생성합니다."""
        print("\n" + "="*60)
        print("📈 ChatFood 로그 분석 리포트")
        print("="*60)
        
        # 기본 통계
        print(f"\n📊 기본 통계:")
        print(f"  총 로그 수: {self.stats['total_logs']:,}")
        print(f"  INFO: {self.stats['info_count']:,}")
        print(f"  WARN: {self.stats['warn_count']:,}")
        print(f"  ERROR: {self.stats['error_count']:,}")
        print(f"  DEBUG: {self.stats['debug_count']:,}")
        
        # 에러율 계산
        if self.stats['total_logs'] > 0:
            error_rate = (self.stats['error_count'] / self.stats['total_logs']) * 100
            print(f"  에러율: {error_rate:.2f}%")
        
        # API 호출 통계
        if self.stats['api_calls']:
            print(f"\n🔌 API 호출 통계:")
            for api, count in sorted(self.stats['api_calls'].items(), key=lambda x: x[1], reverse=True):
                print(f"  {api}: {count:,}회")
        
        # 사용자 액션 통계
        if self.stats['user_actions']:
            print(f"\n👤 사용자 액션 통계:")
            for action, count in sorted(self.stats['user_actions'].items(), key=lambda x: x[1], reverse=True):
                print(f"  {action}: {count:,}회")
        
        # 응답 시간 통계
        if self.stats['response_times']:
            response_times = self.stats['response_times']
            avg_time = sum(response_times) / len(response_times)
            max_time = max(response_times)
            min_time = min(response_times)
            
            print(f"\n⏱️ 응답 시간 통계:")
            print(f"  평균: {avg_time:.2f}ms")
            print(f"  최대: {max_time}ms")
            print(f"  최소: {min_time}ms")
            
            # 느린 요청 (1초 이상) 카운트
            slow_requests = len([t for t in response_times if t > 1000])
            if slow_requests > 0:
                print(f"  느린 요청 (1초 이상): {slow_requests}개")
        
        # 시간대별 통계
        if self.stats['hourly_stats']:
            print(f"\n🕐 시간대별 활동:")
            for hour in sorted(self.stats['hourly_stats'].keys()):
                count = self.stats['hourly_stats'][hour]
                bar = "█" * (count // 10)  # 간단한 막대 그래프
                print(f"  {hour:02d}시: {count:4d}회 {bar}")
        
        # 에러 패턴 분석
        if self.stats['error_patterns']:
            print(f"\n🚨 주요 에러 패턴:")
            for pattern, count in self.stats['error_patterns'].most_common(5):
                print(f"  {pattern}: {count}회")
        
        print(f"\n✅ 분석 완료!")
        print("="*60)

def main():
    parser = argparse.ArgumentParser(description='ChatFood 로그 분석 도구')
    parser.add_argument('log_file', help='분석할 로그 파일 경로')
    parser.add_argument('--json', action='store_true', help='JSON 형태로 출력')
    
    args = parser.parse_args()
    
    analyzer = LogAnalyzer(args.log_file)
    analyzer.analyze_logs()

if __name__ == "__main__":
    main()

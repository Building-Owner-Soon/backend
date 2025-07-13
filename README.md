# 내 꿈은 건물주
**관계 속 에피소드를 금융 히스토리로 기록하고 공유하는 서비스입니다.**

친구/연인/가족과의 돈 거래를 정리하고 신뢰를 기반으로 상환 흐름을 함께 추적하거나

가까운 관계에서 발생한 소비를 추억으로 남기며, 기억과 기록 중심의 새로운 금융 경험을 만들어보세요!

> 💡 개인적인 기록부터, 서로 함께 관리하는 거래까지 모두 포괄하는 유연한 구조를 지향합니다.

## Stack
- Kotlin, Spring, RDB(MySQL or MariaDB), webflux 이용하여 개발
- ERD는 [MermaidChart](https://www.mermaidchart.com/app/projects/5267881a-9ee8-4522-87a2-e705b3a157fe/diagrams/33d611c9-cea2-4784-81a9-38408ea990ac/version/v0.1/edit)를 이용하여 관리

## Architecture
- Domain Driven Design

## Convention
- ktlint, detekt을 이용해 기본 컨벤션 관리
    - pre-commit hook으로 diff 파일 체크, pre-push hook을 통해 전체 파일 체크

### Git Branch 전략
```
master: production
staging: QA
develop: dev
```

### Branch & Commit
```
feature: 기능 추가
fix: 버그
hotfix: 핫픽스
chore: 설정
docs: 문서
style: 코드 스타일, 형식
```



# React Frontend

React + Vite 기반의 프론트엔드 프로젝트입니다.

## 주요 기능

- ⚡ Vite로 빠른 개발 환경
- ⚛️ React 19
- 🛣️ React Router로 라우팅
- 🎨 모던한 UI/UX
- 📦 Axios 기반 API 호출
- 🔄 커스텀 훅 제공

## 프로젝트 구조

```
src/
├── components/     # 재사용 가능한 컴포넌트
├── pages/          # 페이지 컴포넌트
├── hooks/          # 커스텀 훅
├── services/       # API 서비스
├── utils/          # 유틸리티 함수
└── assets/         # 정적 파일
```

## 시작하기

### 설치

```bash
npm install
```

### 개발 서버 실행

```bash
npm run dev
```

### 빌드

```bash
npm run build
```

### 프리뷰

```bash
npm run preview
```

## 환경 변수

`.env` 파일을 생성하고 다음을 설정하세요:

```env
VITE_API_URL=http://localhost:3000/api
```

## 주요 디렉터리 설명

- **components/**: 재사용 가능한 UI 컴포넌트
- **pages/**: 라우트별 페이지 컴포넌트
- **hooks/**: 데이터 fetching 등 커스텀 훅
- **services/**: API 호출 서비스 레이어
- **utils/**: 공통 유틸리티 및 API 클라이언트 설정

## 기술 스택

- React 19
- Vite 7
- React Router DOM
- Axios

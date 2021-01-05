# AGAPP

### 몰입캠프 1주차 Android App



#### 첫번째 Tab

Fragment 기반의 View를 사용

핸드폰 내부 데이터베이스를 이용하여 연락처 추가, 제거, 검색 기능 구현

#### 두번째 Tab 

Fragment 기반의 View를 사용

핸드폰 내부 데이터베이스를 이용하여 갤러리 이미지를 최초 10개 불러옴

이후 사용자가 RecyclerView의 Slide Bar를 내릴때마다 추가로 10개씩 더 불러옴


#### 세번째 Tab

Fragment 기반 View 사용

Volley와 Youtube Data API를 이용하여, Youtube Data를 얻고, 이를 사용자에게 보여줌.

이후 동영상은 YoutubePlayer View를 이용하여 재생

추가적으로 사용자는 재생목록과 앞으로 시청할 동영상 목록을 생성 가능.

---

#### Library Dependency

Room-Runtime 2.2.5

Volley 1.1.1

Glide 4.11.0

PRND Youtube Player View 1.4.0

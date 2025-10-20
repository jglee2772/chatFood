import pandas as pd
import numpy as np
import tensorflow as tf
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder, MultiLabelBinarizer
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout
from tensorflow.keras.callbacks import EarlyStopping
from tensorflow.keras.optimizers import Adam
import keras_tuner as kt
import os
import joblib # 모델 객체 저장을 위해 추가

# 경고 메시지 숨기기
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
tf.get_logger().setLevel('ERROR')

# --- 1. 데이터 생성 (이전과 동일) ---
np.random.seed(42)
n_samples = 20000
genders = ["남자", "여자"]
ages = ["10대", "20대", "30대", "40대", "50대 이상"]
regions = ["서울", "부산", "경기", "인천", "기타"]
categories = ["한식", "중식", "일식", "양식", "분식"]
food_price_range_map = {
    "제육볶음": (9000, 12000), "순대국밥": (8000, 11000), "비빔밥": (9000, 12000), "된장찌개": (8000, 10000),
    "짜장면": (7000, 9000), "짬뽕": (8000, 11000), "탕수육": (14000, 20000),
    "초밥": (14000, 22000), "라멘": (10000, 14000), "돈까스": (9000, 13000),
    "파스타": (13000, 19000), "피자": (17000, 25000), "스테이크": (22000, 35000),
    "떡볶이": (4000, 7000), "김밥": (3500, 5000), "라면": (4000, 6000),
}
all_data = []
n_choice_samples = int(n_samples * 0.8)
for _ in range(n_choice_samples):
    gender, age, region = np.random.choice(genders), np.random.choice(ages), np.random.choice(regions)
    fav_categories = list(np.random.choice(categories, np.random.randint(1, 4), replace=False))
    pref_category = np.random.choice(fav_categories) if np.random.rand() > 0.05 else np.random.choice(categories)
    food = None
    if pref_category == "한식":
        food = np.random.choice(["제육볶음", "순대국밥"] if gender == "남자" else ["비빔밥", "된장찌개"], p=[0.85, 0.15])
    elif pref_category == "중식":
        food = np.random.choice(["짜장면", "탕수육"] if age in ["10대", "20대"] else ["짬뽕", "탕수육"], p=[0.9, 0.1])
    elif pref_category == "일식":
        if region == "부산": food = "초밥"
        elif gender == "여자": food = np.random.choice(["라멘", "초밥"], p=[0.8, 0.2])
        else: food = np.random.choice(["돈까스", "라멘"], p=[0.8, 0.2])
    elif pref_category == "양식":
        food = "스테이크" if age in ["40대", "50대 이상"] else np.random.choice(["파스타", "피자"], p=[0.8, 0.2])
    else:
        food = "떡볶이" if age == "10대" else np.random.choice(["김밥", "라면"], p=[0.7, 0.3])
    price = np.random.randint(*food_price_range_map.get(food, (0, 0))) if food else 0
    all_data.append([gender, age, region, fav_categories, pref_category, food, price])
n_no_choice_samples = n_samples - n_choice_samples
for _ in range(n_no_choice_samples):
    all_data.append([np.random.choice(genders), np.random.choice(ages), np.random.choice(regions), list(np.random.choice(categories, np.random.randint(1, 4), replace=False)), np.random.choice(categories), "선택안함", 0])
df = pd.DataFrame(all_data, columns=["성별", "나이대", "지역", "좋아하는카테고리", "선호카테고리", "음식", "가격"]).sample(frac=1).reset_index(drop=True)

# --- 2. 데이터 전처리 (이전과 동일) ---
X = df[["성별", "나이대", "지역", "좋아하는카테고리", "선호카테고리"]]
y = df["음식"]
mlb = MultiLabelBinarizer()
fav_cat_encoded = mlb.fit_transform(X['좋아하는카테고리'])
fav_cat_df = pd.DataFrame(fav_cat_encoded, columns=mlb.classes_, index=X.index)
other_features = pd.get_dummies(X.drop('좋아하는카테고리', axis=1))
X_processed = pd.concat([other_features, fav_cat_df], axis=1).astype(np.float32)
le = LabelEncoder()
y_encoded = le.fit_transform(y)
X_train, X_test, y_train, y_test = train_test_split(X_processed, y_encoded, test_size=0.2, random_state=42, stratify=y_encoded)
model_columns = X_processed.columns # 나중에 API에서 컬럼 순서를 맞추기 위해 저장

# --- 3. 모델 최적화 및 학습 (이전과 동일) ---
def build_model(hp):
    model = Sequential([
        Dense(hp.Int('units_1', 32, 512, 32), activation='relu', input_shape=(X_train.shape[1],)),
        Dropout(hp.Float('dropout_1', 0.1, 0.5, 0.1)),
        Dense(hp.Int('units_2', 32, 256, 32), activation='relu'),
        Dense(len(le.classes_), activation='softmax')
    ])
    model.compile(optimizer=Adam(hp.Choice('learning_rate', [1e-2, 1e-3, 1e-4])), loss='sparse_categorical_crossentropy', metrics=['accuracy'])
    return model

tuner = kt.Hyperband(build_model, objective='val_accuracy', max_epochs=10, factor=3, directory='model_tuning', project_name='food_rec')
stop_early = EarlyStopping(monitor='val_loss', patience=5)
tuner.search(X_train, y_train, epochs=20, validation_split=0.2, callbacks=[stop_early], verbose=1)
best_hps = tuner.get_best_hyperparameters(num_trials=1)[0]
model = tuner.hypermodel.build(best_hps)
history = model.fit(X_train, y_train, epochs=50, validation_split=0.2, callbacks=[stop_early], verbose=1)

# --- 4. 최종 평가 (이전과 동일) ---
loss, accuracy = model.evaluate(X_test, y_test, verbose=0)
print(f"\n✅ 최종 모델 테스트 정확도: {accuracy * 100:.2f}%")

# --- 5. 학습된 모델 및 전처리기 저장 ---
print("\n💾 모델과 전처리기를 파일로 저장합니다...")
model.save('food_recommendation_model.h5')
joblib.dump(le, 'label_encoder.joblib')
joblib.dump(mlb, 'multi_label_binarizer.joblib')
joblib.dump(model_columns, 'model_columns.joblib')
joblib.dump(food_price_range_map, 'food_price_map.joblib') # 가격 정보도 저장
print("✅ 저장 완료!")
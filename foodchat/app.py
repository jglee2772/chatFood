import os
import numpy as np
import pandas as pd
import tensorflow as tf
import joblib
from flask import Flask, request, jsonify
from flask_cors import CORS

# --- 0. 환경 설정 및 초기화 ---
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
tf.get_logger().setLevel('ERROR')

app = Flask(__name__)
CORS(app)

# --- 1. 모델 로딩 ---
try:
    print("🧠 AI 추천 모델과 전처리기를 로딩합니다...")
    model = tf.keras.models.load_model('food_recommendation_model.h5')
    le = joblib.load('label_encoder.joblib')
    model_columns = joblib.load('model_columns.joblib')
    food_price_range_map = joblib.load('food_price_map.joblib')
    print(" AI 추천 모델 로딩 완료!")
except Exception as e:
    print(f" AI 추천 모델 로딩 중 치명적 오류 발생: {e}")
    model = None

# --- 2. 추천 로직 함수 (수정됨) ---
def recommend_food_logic(data):
    # Java에서 받은 데이터의 키를 모델이 학습한 한글 컬럼명으로 변환
    key_map = {
        "gender": "성별", "ageGroup": "나이대", "region": "지역",
        "prefCategory": "선호카테고리", "favCategories": "좋아하는카테고리"
    }
    korean_data = {key_map.get(k, k): v for k, v in data.items()}

    processed_df = pd.DataFrame(0, index=[0], columns=model_columns)
    
    for feature in ["성별", "나이대", "지역", "선호카테고리"]:
        value = korean_data.get(feature)
        if value:
            column_name = f"{feature}_{value}"
            if column_name in processed_df.columns:
                processed_df.loc[0, column_name] = 1.0
    
    fav_categories = korean_data.get("좋아하는카테고리")
    if fav_categories and isinstance(fav_categories, list):
        for category in fav_categories:
            if category in processed_df.columns:
                processed_df.loc[0, category] = 1.0
    
    prediction_probs = model.predict(processed_df.astype(np.float32), verbose=0)[0]
    top_5_indices = np.argsort(prediction_probs)[::-1][:5]
    top_5_foods = le.inverse_transform(top_5_indices)
    
    # ---  핵심 수정: 더욱 안정적인 추천 로직 ---
    # 1. Top 5 목록에서 '선택안함'을 먼저 걸러냅니다.
    valid_recommendations = [food for food in top_5_foods if food != "선택안함"]
    
    # 2. 걸러낸 목록에서 최대 3개를 최종 추천 메뉴로 선택합니다.
    final_top_3 = valid_recommendations[:3]
    
    response = {"status": "success", "recommendations": []}

    if not final_top_3:
        # 유효한 추천 메뉴가 하나도 없을 때만 'no_recommendation' 상태를 보냅니다.
        response['status'] = 'no_recommendation'
    else:
        for food in final_top_3:
            price_range = food_price_range_map.get(food, ("정보 없음", "정보 없음"))
            response['recommendations'].append({
                "food_name": food, 
                "price_min": price_range[0], 
                "price_max": price_range[1]
            })
            
    return response

# --- 3. API 엔드포인트 ---
@app.route('/recommend', methods=['POST'])
def recommend_api():
    if model is None:
        return jsonify({'error': '모델이 로드되지 않았습니다.'}), 500
    try:
        data = request.get_json()
        if not data:
            return jsonify({'error': '잘못된 요청입니다: JSON 데이터가 없습니다.'}), 400
            
        recommendations = recommend_food_logic(data)
        return jsonify(recommendations)
    except Exception as e:
        print(f" 추천 처리 중 오류 발생: {e}")
        return jsonify({'error': '서버 내부 오류가 발생했습니다.'}), 500

# --- 4. 서버 실행 ---
if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=False)


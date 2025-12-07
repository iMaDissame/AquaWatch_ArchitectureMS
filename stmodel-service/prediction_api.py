"""
AquaWatch - API de Prédiction de Qualité de l'Eau
Flask API pour le modèle de Machine Learning

Lancer avec: python prediction_api.py
Port: 5000
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import numpy as np

app = Flask(__name__)
CORS(app)

# Seuils pour chaque paramètre (valeurs normales)
THRESHOLDS = {
    'ph': {'min': 6.5, 'max': 8.5, 'optimal_min': 7.0, 'optimal_max': 7.5},
    'turbidity': {'min': 0, 'max': 5, 'optimal_max': 1},
    'dissolvedOxygen': {'min': 6, 'max': 14, 'optimal_min': 8},
    'temperature': {'min': 15, 'max': 30, 'optimal_min': 20, 'optimal_max': 25},
    'conductivity': {'min': 200, 'max': 800, 'optimal_min': 300, 'optimal_max': 500},
    'nitrates': {'min': 0, 'max': 10, 'optimal_max': 5},
    'phosphates': {'min': 0, 'max': 0.1, 'optimal_max': 0.05},
    'chlorophyll': {'min': 0, 'max': 10, 'optimal_max': 4}
}

def calculate_parameter_score(param_name, value):
    """Calcule le score d'un paramètre (0-100)"""
    if param_name not in THRESHOLDS:
        return 50
    
    t = THRESHOLDS[param_name]
    
    # Vérifier si la valeur est dans les limites acceptables
    if 'min' in t and value < t['min']:
        # En dessous du minimum
        deviation = (t['min'] - value) / t['min'] * 100
        return max(0, 100 - deviation * 2)
    
    if 'max' in t and value > t['max']:
        # Au dessus du maximum
        deviation = (value - t['max']) / t['max'] * 100
        return max(0, 100 - deviation * 2)
    
    # Dans la plage acceptable, vérifier si optimal
    if 'optimal_min' in t and 'optimal_max' in t:
        if t['optimal_min'] <= value <= t['optimal_max']:
            return 100
        elif value < t['optimal_min']:
            return 70 + 30 * (value - t['min']) / (t['optimal_min'] - t['min'])
        else:
            return 70 + 30 * (t['max'] - value) / (t['max'] - t['optimal_max'])
    elif 'optimal_max' in t:
        if value <= t['optimal_max']:
            return 100
        else:
            return 70 + 30 * (t['max'] - value) / (t['max'] - t['optimal_max'])
    elif 'optimal_min' in t:
        if value >= t['optimal_min']:
            return 100
        else:
            return 70 + 30 * (value - t['min']) / (t['optimal_min'] - t['min'])
    
    return 80  # Par défaut si dans la plage

def get_recommendations(param_scores, params):
    """Génère des recommandations basées sur les scores"""
    recommendations = []
    
    for param, score in param_scores.items():
        value = params.get(param, 0)
        t = THRESHOLDS.get(param, {})
        
        if score < 50:
            # Problème critique
            if param == 'ph':
                if value < 6.5:
                    recommendations.append(f"⚠️ pH trop acide ({value}). Ajouter des agents alcalinisants.")
                else:
                    recommendations.append(f"⚠️ pH trop basique ({value}). Ajouter des agents acidifiants.")
            
            elif param == 'turbidity':
                recommendations.append(f"⚠️ Turbidité élevée ({value} NTU). Vérifier les sources de pollution et améliorer la filtration.")
            
            elif param == 'dissolvedOxygen':
                recommendations.append(f"⚠️ Oxygène dissous faible ({value} mg/L). Améliorer l'aération du plan d'eau.")
            
            elif param == 'temperature':
                if value > 30:
                    recommendations.append(f"⚠️ Température élevée ({value}°C). Surveiller l'eutrophisation.")
                else:
                    recommendations.append(f"⚠️ Température basse ({value}°C). Possible impact sur la faune aquatique.")
            
            elif param == 'conductivity':
                recommendations.append(f"⚠️ Conductivité anormale ({value} µS/cm). Vérifier la salinité et les polluants minéraux.")
            
            elif param == 'nitrates':
                recommendations.append(f"⚠️ Nitrates élevés ({value} mg/L). Réduire les apports agricoles, contrôler les rejets.")
            
            elif param == 'phosphates':
                recommendations.append(f"⚠️ Phosphates élevés ({value} mg/L). Risque d'eutrophisation. Limiter les rejets domestiques.")
            
            elif param == 'chlorophyll':
                recommendations.append(f"⚠️ Chlorophylle élevée ({value} µg/L). Bloom algal possible. Surveiller l'oxygène dissous.")
        
        elif score < 70:
            # Problème modéré
            if param == 'ph':
                recommendations.append(f"ℹ️ pH légèrement hors norme ({value}). Surveillance recommandée.")
            elif param == 'turbidity':
                recommendations.append(f"ℹ️ Turbidité modérée ({value} NTU). Surveillance recommandée.")
            elif param == 'dissolvedOxygen':
                recommendations.append(f"ℹ️ Oxygène dissous à surveiller ({value} mg/L).")
            elif param == 'nitrates':
                recommendations.append(f"ℹ️ Nitrates modérés ({value} mg/L). Surveiller l'évolution.")
            elif param == 'phosphates':
                recommendations.append(f"ℹ️ Phosphates modérés ({value} mg/L). Surveiller l'évolution.")
    
    if not recommendations:
        recommendations.append("✅ Tous les paramètres sont dans les normes. Continuer la surveillance régulière.")
    
    return recommendations

def predict_quality(params):
    """Prédit la qualité de l'eau basée sur les paramètres"""
    
    # Calculer le score de chaque paramètre
    param_scores = {}
    for param in ['ph', 'turbidity', 'dissolvedOxygen', 'temperature', 
                  'conductivity', 'nitrates', 'phosphates', 'chlorophyll']:
        if param in params and params[param] is not None:
            param_scores[param] = calculate_parameter_score(param, params[param])
    
    # Score global (moyenne pondérée)
    weights = {
        'ph': 1.5,
        'turbidity': 1.2,
        'dissolvedOxygen': 1.5,
        'temperature': 0.8,
        'conductivity': 0.8,
        'nitrates': 1.3,
        'phosphates': 1.3,
        'chlorophyll': 1.0
    }
    
    total_weight = 0
    weighted_sum = 0
    for param, score in param_scores.items():
        w = weights.get(param, 1)
        weighted_sum += score * w
        total_weight += w
    
    overall_score = weighted_sum / total_weight if total_weight > 0 else 50
    
    # Déterminer le statut
    if overall_score >= 80:
        status = "GOOD"
        details = "La qualité de l'eau est excellente."
    elif overall_score >= 60:
        status = "MODERATE"
        details = "La qualité de l'eau est acceptable mais nécessite une surveillance."
    else:
        status = "BAD"
        details = "La qualité de l'eau est mauvaise. Action requise."
    
    # Générer les recommandations
    recommendations = get_recommendations(param_scores, params)
    
    return {
        'score': round(overall_score, 1),
        'status': status,
        'details': details,
        'parameterScores': {k: round(v, 1) for k, v in param_scores.items()},
        'recommendations': recommendations
    }

@app.route('/predict', methods=['POST'])
def predict():
    """Endpoint de prédiction"""
    try:
        data = request.json
        
        if not data:
            return jsonify({'error': 'No data provided'}), 400
        
        # Extraire les paramètres
        params = {
            'ph': data.get('ph'),
            'turbidity': data.get('turbidity'),
            'dissolvedOxygen': data.get('dissolvedOxygen'),
            'temperature': data.get('temperature'),
            'conductivity': data.get('conductivity'),
            'nitrates': data.get('nitrates'),
            'phosphates': data.get('phosphates'),
            'chlorophyll': data.get('chlorophyll')
        }
        
        # Filtrer les valeurs nulles
        params = {k: v for k, v in params.items() if v is not None}
        
        if not params:
            return jsonify({'error': 'No valid parameters provided'}), 400
        
        result = predict_quality(params)
        
        return jsonify(result)
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/health', methods=['GET'])
def health():
    """Health check endpoint"""
    return jsonify({'status': 'UP', 'service': 'prediction-api'})

if __name__ == '__main__':
    print("=" * 50)
    print("🌊 AquaWatch - Prediction API")
    print("=" * 50)
    print("Starting server on http://localhost:5000")
    print("Endpoints:")
    print("  POST /predict - Predict water quality")
    print("  GET  /health  - Health check")
    print("=" * 50)
    app.run(host='0.0.0.0', port=5000, debug=True)

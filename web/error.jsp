<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Sistema de Tokens</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        .error-container {
            background: white;
            border-radius: 20px;
            padding: 60px 40px;
            max-width: 600px;
            width: 100%;
            text-align: center;
            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
        }
        
        .error-icon {
            font-size: 80px;
            color: #ef4444;
            margin-bottom: 20px;
            animation: shake 0.5s;
        }
        
        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-10px); }
            75% { transform: translateX(10px); }
        }
        
        h1 {
            font-size: 36px;
            color: #1f2937;
            margin-bottom: 10px;
        }
        
        .error-code {
            font-size: 18px;
            color: #6b7280;
            margin-bottom: 20px;
        }
        
        .error-message {
            font-size: 16px;
            color: #4b5563;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        
        .error-details {
            background: #f3f4f6;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
            text-align: left;
        }
        
        .error-details p {
            font-size: 14px;
            color: #6b7280;
            margin-bottom: 10px;
            word-break: break-all;
        }
        
        .error-actions {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 14px 30px;
            border-radius: 10px;
            text-decoration: none;
            font-size: 15px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(30, 64, 175, 0.4);
        }
        
        .btn-secondary {
            background: #f3f4f6;
            color: #4b5563;
        }
        
        .btn-secondary:hover {
            background: #e5e7eb;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">
            <i class="fas fa-exclamation-triangle"></i>
        </div>
        
        <h1>¡Oops! Algo salió mal</h1>
        
        <%
            String errorCode = request.getAttribute("javax.servlet.error.status_code") != null 
                ? request.getAttribute("javax.servlet.error.status_code").toString() 
                : "500";
            
            String errorMessage = "";
            switch(errorCode) {
                case "404":
                    errorMessage = "La página que buscas no existe";
                    break;
                case "500":
                    errorMessage = "Ha ocurrido un error interno en el servidor";
                    break;
                case "403":
                    errorMessage = "No tienes permiso para acceder a este recurso";
                    break;
                default:
                    errorMessage = "Ha ocurrido un error inesperado";
            }
            
            Exception exception1 = (Exception) request.getAttribute("javax.servlet.error.exception");
        %>
        
        <div class="error-code">
            Error <%= errorCode %>
        </div>
        
        <div class="error-message">
            <%= errorMessage %>. Por favor, intenta nuevamente o contacta al administrador del sistema.
        </div>
        
        <% if (exception != null && "true".equals(request.getParameter("debug"))) { %>
        <div class="error-details">
            <p><strong>Tipo:</strong> <%= exception.getClass().getName() %></p>
            <p><strong>Mensaje:</strong> <%= exception.getMessage() %></p>
        </div>
        <% } %>
        
        <div class="error-actions">
            <a href="javascript:history.back()" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i>
                Volver Atrás
            </a>
            <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn btn-primary">
                <i class="fas fa-home"></i>
                Ir al Inicio
            </a>
        </div>
    </div>
</body>
</html>
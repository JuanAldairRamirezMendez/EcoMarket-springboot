$body = @{ username='testuser'; email='test@example.com'; password='TestPass123' } | ConvertTo-Json
try {
  $resp = Invoke-RestMethod -Uri 'http://localhost:9092/ecomarket/api/auth/register' -Method Post -Body $body -ContentType 'application/json' -TimeoutSec 10
  Write-Host "Registered token: $($resp.token)"
  $token = $resp.token
  try {
     $r = Invoke-RestMethod -Uri 'http://localhost:9092/ecomarket/api/products' -Method Get -Headers @{ Authorization = "Bearer $token" } -TimeoutSec 10
     Write-Host "Products:"
     $r | ConvertTo-Json -Depth 5
  } catch {
     Write-Host "products failed after register: $($_.Exception.Message)"
  }
} catch {
  Write-Host "register failed: $($_.Exception.Message)"
  Write-Host "Attempting login..."
  $loginBody = @{ usernameOrEmail='admin'; password='AdminPass123' } | ConvertTo-Json
  try {
    $loginResp = Invoke-RestMethod -Uri 'http://localhost:9092/ecomarket/api/auth/login' -Method Post -Body $loginBody -ContentType 'application/json' -TimeoutSec 10
    Write-Host "Login token: $($loginResp.token)"
    $token = $loginResp.token
    try {
      $r = Invoke-RestMethod -Uri 'http://localhost:9092/ecomarket/api/products' -Method Get -Headers @{ Authorization = "Bearer $token" } -TimeoutSec 10
      Write-Host "Products after login:"
      $r | ConvertTo-Json -Depth 5
    } catch {
      Write-Host "products failed after login: $($_.Exception.Message)"
    }
  } catch {
    Write-Host "login failed: $($_.Exception.Message)"
  }
}

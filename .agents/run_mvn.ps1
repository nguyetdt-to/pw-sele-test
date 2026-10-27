$projectDir = "d:\GIT\tablet-automationtest\vietcombank-selenium-cucumber"
$outputFile = "d:\GIT\tablet-automationtest\.agents\mvn_output.txt"

Push-Location $projectDir
try {
    $result = & mvn clean compile test-compile 2>&1
    $result | Out-File -FilePath $outputFile -Encoding UTF8
    Write-Host "Exit code: $LASTEXITCODE"
    Write-Host "Output written to: $outputFile"
} finally {
    Pop-Location
}

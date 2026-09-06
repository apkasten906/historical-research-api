# Runs on the Windows host before VS Code starts the Dev Container.
[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Show-UpdateNotification {
    param(
        [string]$Message,
        [bool]$Failed = $false
    )

    Write-Host "[WSL update] $Message"
    $popup = $null
    try {
        $popup = New-Object -ComObject WScript.Shell
        # Timed popup: no extra module, app registration, or click required.
        $icon = if ($Failed) { 48 } else { 64 }
        $null = $popup.Popup($Message, 8, 'Dev Container - WSL update', $icon)
    }
    catch {
        Write-Warning "Windows popup unavailable. See the WSL update message above."
    }
    finally {
        if ($null -ne $popup) {
            $null = [System.Runtime.InteropServices.Marshal]::ReleaseComObject($popup)
        }
    }
}

try {
    $wsl = (Get-Command wsl.exe -CommandType Application -ErrorAction Stop).Source
    Write-Host '[WSL update] Checking for and installing WSL updates before container startup...'
    # Inherit console output so progress and any Windows prompts remain visible.
    $update = Start-Process -FilePath $wsl -ArgumentList '--update' -NoNewWindow -Wait -PassThru
    if ($update.ExitCode -eq 3010) {
        Show-UpdateNotification -Failed $true -Message 'WSL requested a Windows reboot. Restart Windows, then reopen the project in the container. Container startup has stopped.'
        exit 1
    }
    if ($update.ExitCode -ne 0) {
        throw "wsl --update exited with code $($update.ExitCode)."
    }
}
catch {
    Show-UpdateNotification -Failed $true -Message "WSL update failed. Container startup has stopped. $($_.Exception.Message) See the Dev Containers log. Run 'wsl --update' in Windows PowerShell (as Administrator if required), then retry."
    exit 1
}

Show-UpdateNotification -Message 'WSL update check completed successfully; any available update was installed. Continuing container startup. If WSL reported that a restart is required, restart Windows before using the container.'
exit 0

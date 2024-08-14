{{/*
Return the name of the chart
*/}}
{{- define "agent-service.name" -}}
{{ .Chart.Name | trunc 63 | trimSuffix "-" }}
{{- end -}}

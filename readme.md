# 🚀 Jenkins Shared Library – Trivy + DefectDojo Pipeline

This repository contains a **Jenkins Shared Library** for building Docker images, scanning them with **Trivy**, enforcing a **security gate**, and uploading results to **DefectDojo**.

---

## 📦 Features

* ✅ Docker image build
* 🔍 Trivy security scan (vulnerabilities, secrets, misconfig)
* 🚨 Security gate (fail pipeline on HIGH/CRITICAL)
* 📊 Upload scan results to DefectDojo
* 📁 Archive scan report as artifact
* ♻️ Reusable across multiple projects

---

## 📁 Project Structure

```
jenkins-shared-lib/
└── vars/
    ├── buildDocker.groovy
    ├── trivyScan.groovy
    ├── securityGate.groovy
    ├── uploadDefectDojo.groovy
    └── trivyDefectdojoPipeline.groovy
```

---

## ⚙️ Jenkins Configuration

### 1. Add Shared Library

Go to:

```
Manage Jenkins → System → Global Pipeline Libraries
```

Add:

* **Name**: `share_lib`
* **Default version**: `main`
* **Retrieval method**: Git
* **Repository URL**:
  `https://github.com/your-username/jenkins-shared-lib.git`

---

### 2. Required Jenkins Credentials

| ID           | Description              |
| ------------ | ------------------------ |
| `DEFECTDOJO` | API token for DefectDojo |

---

### 3. Jenkins Agent Requirements

Your Jenkins agent must have:

* Docker installed
* Docker Compose installed
* Access to Trivy container (`docker compose`)
* Permission to run Docker commands

---

## 🧪 Usage Example (Jenkinsfile)

```groovy
@Library('share_lib') _

trivyDefectdojoPipeline(
    agentLabel: 'trivy',
    gitUrl: 'https://github.com/chengdevith/jobservice.git',
    gitBranch: 'main',
    imageName: 'jobservice',
    trivyPath: '/home/enz/trivy/docker-compose.yml',
    defectdojoUrl: 'https://defectdojo.devith.it.com',
    defectdojoCredentialId: 'DEFECTDOJO',
    productTypeName: 'Web Applications',
    productName: 'JobFinder',
    engagementName: 'Jenkins-CI',
    testTitle: 'Trivy Image Scan',
    gateSeverity: 'HIGH,CRITICAL'
)
```

---

## 🔐 Security Gate

The pipeline will fail if vulnerabilities match the configured severity.

### Example:

```groovy
gateSeverity: 'HIGH,CRITICAL'
```

Options:

* `LOW`
* `MEDIUM`
* `HIGH`
* `CRITICAL`

---

## 🔄 Pipeline Flow

```
Checkout Code
      ↓
Build Docker Image
      ↓
Trivy Scan (JSON Report)
      ↓
Upload to DefectDojo
      ↓
Security Gate (Fail if vulnerable)
      ↓
Archive Report
```

---

## 📊 DefectDojo Integration

The pipeline automatically:

* Creates product (if not exists)
* Creates engagement
* Imports scan results
* Closes old findings

---

## 📁 Output

* `trivy-report.json` is generated and archived in Jenkins

---

## 🛠 Customization

You can override parameters:

| Parameter       | Description                          |
| --------------- | ------------------------------------ |
| `imageName`     | Docker image name                    |
| `gitUrl`        | Repository URL                       |
| `trivyPath`     | Path to docker-compose.yml for Trivy |
| `defectdojoUrl` | DefectDojo server                    |
| `gateSeverity`  | Security threshold                   |

---

## ⚠️ Notes

* Ensure Trivy container is running before pipeline
* Ensure Jenkins user has Docker permission
* DefectDojo API must be reachable
* Use HTTPS or `-k` flag for self-signed certs

---

## 🔥 Future Improvements

* Push image to Harbor
* Slack/Telegram notification
* Parallel scanning
* Multi-image support
* Helm/Kubernetes deployment stage

---

## 👨‍💻 Author

Cheng Devith – DevOps Engineer 🚀

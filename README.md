# VisioN — Sistema de Visão e Alinhamento por AprilTags (FRC #9195)

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![WPILib](https://img.shields.io/badge/WPILib-2025-red?style=flat-square)
![PhotonVision](https://img.shields.io/badge/PhotonVision-AprilTags-00A8E8?style=flat-square)
![REVLib](https://img.shields.io/badge/REVLib-SparkMax-orange?style=flat-square)

Projeto desenvolvido em **Java (WPILib)** para a equipe **Prodixy (#9195)** focado em **Visão Computacional**, telemetria avançada no **Shuffleboard** e **alinhamento autônomo/assistido** utilizando **PhotonVision** e **AprilTags**.

---

## Funcionalidades Principais

### 1. Alinhamento Automático por AprilTag (`AlignToAprilTagCommand` & `AutoAlignCommand`)
* Lê os pacotes mais recentes da câmera **PhotonVision** (`aprilcam`) utilizando `getAllUnreadResults()`.
* Calcula o erro angular horizontal (`Yaw / tx`) até o melhor alvo detectado.
* Aplica controle proporcional ($k_P$) com compensação de zona morta (`MinPower`) para vencer o atrito estático do robô.
* Permite **calibração ao vivo pelo Shuffleboard** sem precisar recompilar o código (`kP` e `MinPower` editáveis na aba *Vision*).

### 2. Dashboard Completo no Shuffleboard (`RobotContainer`)
O código configura automaticamente abas dedicadas para a equipe de pilotagem:
* **Aba `Driver`:**
  * Stream ao vivo da câmera USB do piloto (`320x240 @ 30 FPS`) via `CameraServer`.
* **Aba `Vision`:**
  * Stream HTTP do **PhotonVision** (`http://photonvision.local:5800/stream.mjpg`).
  * Indicadores em tempo real: **Tem Alvo?**, **Yaw (tx)**, **Pitch (ty)**, **Área do Alvo** e **Timestamp (ms)**.
* **Aba `Drive Metrics`:**
  * Velocidade e tensão aplicada nos motores esquerdos e direitos (`SparkMax` IDs `1` a `4`).
  * Estimativa de velocidade linear ($m/s$) e orientação acumulada (`Yaw°`).

### 3. Tração com Modo Boost (`DriveSubsystem` & `JoystickDriveCommand`)
* Controle **Arcade Drive** com motores configurados em modo `Brake`, rampa de malha aberta (`0.3s`) e limite inteligente de corrente (`40A`).
* Suporte a multiplicador de velocidade (**Boost**) acionado pelo piloto.

---

## Controles (Joystick - Porta 0)

| Controle | Ação |
| :--- | :--- |
| **Joystick Eixos X / Y** | Movimentação *Arcade Drive* (`JoystickDriveCommand`) |
| **Botão `2` (Segurar)** | Aciona o alinhamento automático na AprilTag (`AlignToAprilTagCommand`) |

---

## Como Executar

1. Abra o projeto no **WPILib VS Code**.
2. Compile o projeto:
   ```bash
   ./gradlew build
   ```
3. Faça o deploy para o robô (Equipe **#9195**):
   ```bash
   ./gradlew deploy
   ```

---

Desenvolvido por **Vinicius Albuquerque** — Equipe **Prodixy #9195**

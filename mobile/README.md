# Carimbox - Android Nativo (Kotlin & Jetpack Compose)

Aplicativo nativo para Android do **Carimbos Ocian**, desenvolvido em **Kotlin puro** com a interface gráfica moderna em **Jetpack Compose (Material 3)** e comunicação direta com o banco de dados via **Supabase**.

---

## 🚀 Tecnologias e Arquitetura

- **Linguagem**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose com Material 3 (suporte a Tema Claro e Escuro)
- **Suporte Android**:
  - `minSdk`: 21 (Android 5.0 Lollipop ou superior)
  - `targetSdk`: 35 (Android 15)
  - `compileSdk`: 35
- **Backend**: Supabase (`io.github.jan-tennert.supabase:postgrest-kt` + `ktor-client-okhttp`)
- **Arquitetura**: MVVM (Model-View-ViewModel) com StateFlow e Repository Pattern
- **Navegação**: Jetpack Navigation Compose
- **Injeção de Dependências**: Manual / AppContainer leve
- **Persistência de Preferências**: AndroidX DataStore Preferences (armazenamento do tema escuro)
- **Internacionalização**: Recursos em `res/values/strings.xml` (Português) e `res/values-en/strings.xml` (Inglês)

---

## 📱 Telas e Funcionalidades

1. **Lista de Pedidos (Home)**:
   - Filtros por abas: *Pendentes*, *Concluídos* e *Todos*.
   - Filtros dinâmicos por data: *Dia*, *Mês* e *Ano*.
   - Paginação infinita (carregamento contínuo sob demanda).
   - Pull-to-refresh para atualização rápida.
   - Ação rápida no card para alternar status com diálogo de confirmação.
   - Envio de mensagem de resumo formatada diretamente para o WhatsApp do cliente.
   - Floating Action Button (+) para criação de novos pedidos.

2. **Detalhes do Pedido (`VerPedido`)**:
   - Visualização completa de todos os dados do pedido.
   - Cópia com um clique de dados individuais ou de todos os dados formatados.
   - Botão para iniciar conversa direta no WhatsApp do cliente.
   - Ações de alternância de status, edição e exclusão com diálogo de confirmação.

3. **Novo Pedido (`NovoPedido`)**:
   - Formulário completo com validações (WhatsApp, Descrição/Logo e Valor obrigatórios).
   - Seletor de data moderno para data de entrega.
   - Dropdown de tipos de pagamento e categoria/label (*Carimbo*, *Mdf/Acrílico*, *Outros*).

4. **Editar Pedido (`AtualizarPedido`)**:
   - Carregamento prévio de todos os dados do pedido selecionado.
   - Edição de qualquer campo e salvamento em tempo real no Supabase.

5. **Busca (`Search`)**:
   - Pesquisa por ID numérico do pedido ou por texto (nome do cliente, texto/logo, etc.).
   - Combinação de filtros por período (Dia, Mês, Ano).

6. **Configurações (`Settings`)**:
   - Alternância entre Modo Claro e Modo Escuro persistida localmente.
   - Informações da versão e checagem de atualizações.

---

## ⚙️ Configuração do Ambiente e Execução

### Pré-requisitos
- **Android Studio**: Koala, Ladybug ou mais recente.
- **JDK**: Java 17 ou superior.
- **Android SDK**: API 21 a 35 instaladas pelo SDK Manager.

### Configurando as Credenciais do Supabase

1. Copie o arquivo de exemplo para criar seu `local.properties`:
   ```bash
   cp local.properties.example local.properties
   ```

2. Abra o arquivo `local.properties` e preencha as variáveis com as credenciais do seu projeto Supabase:
   ```properties
   SUPABASE_URL=https://seu-projeto.supabase.co
   SUPABASE_KEY=sua-chave-anon-publica
   ```

### Compilação via Linha de Comando

Para compilar o APK em modo Debug:
```bash
./gradlew assembleDebug
```

O arquivo gerado estará em:
```
mobile/app/build/outputs/apk/debug/app-debug.apk
```

Para instalar diretamente em um dispositivo ou emulador conectado com ADB:
```bash
./gradlew installDebug
```

Attribute VB_Name = "tools"
Option Explicit



Global jsonConverter As New cJsonConverter

Public Function KeyExistsInCollection(ByVal key As String, _
                                      ByVal container As Collection) As Boolean
    With Err
        If container Is Nothing Then .Raise 91
        On Error Resume Next
        Dim temp As Variant
        temp = container.Item(key)
        On Error GoTo 0

        If .Number = 0 Then
            KeyExistsInCollection = True
        ElseIf .Number <> 5 Then
            .Raise .Number
        End If
    End With
End Function

Public Function ParseJson(ByVal jsonString As String) As Object
    Set ParseJson = jsonConverter.ParseJson(jsonString)
End Function

Public Function ConvertToJson(ByVal jsonValue As Variant, Optional ByVal whiteSpace As Variant, Optional ByVal json_CurrentIndentation As Long = 0) As String
    ConvertToJson = jsonConverter.ConvertToJson(jsonValue, whiteSpace, json_CurrentIndentation)
End Function

Function ConsoleClear()
    'Application.SendKeys "^g", True
    'Application.SendKeys "^a", True
    'Application.SendKeys "{DEL}", True
    DoEvents
    Debug.Print "======================================================================================"
End Function

Public Function HasKey(col As Collection, ItemKeyOrNum As Variant) As Boolean
    Dim v As Variant
    On Error Resume Next
    v = IsObject(col.Item(ItemKeyOrNum))
    HasKey = Not IsEmpty(v)
End Function

Public Function Base64Encode(Text As String) As String
    Dim arrData() As Byte
    arrData = StrConv(Text, vbFromUnicode)

    Dim objXML As Variant
    Dim objNode As Variant

    Set objXML = CreateObject("MSXML2.DOMDocument")
    Set objNode = objXML.createElement("b64")

    objNode.DataType = "bin.base64"
    objNode.nodeTypedValue = arrData
    Base64Encode = objNode.Text

    Set objNode = Nothing
    Set objXML = Nothing
End Function

Public Function URLEncode_(StringVal As String, Optional ignoreAmperstand As Boolean = False) As String

    Dim StringLen As Long: StringLen = Len(StringVal)

    If StringLen > 0 Then
        ReDim result(StringLen) As String
        Dim I As Long
        Dim CharCode As Integer
        Dim Char As String
        Dim Space As String

        Space = "%20"

        For I = 1 To StringLen
            Char = Mid$(StringVal, I, 1)
            CharCode = asc(Char)
            If Char = "&" And ignoreAmperstand = True Then
                result(I) = Char
            Else
                Select Case CharCode
                Case 97 To 122, 65 To 90, 48 To 57, 45, 46, 95, 126
                    result(I) = Char
                Case 32
                    result(I) = Space
                Case 0 To 15
                    result(I) = "%0" & Hex$(CharCode)
                Case Else
                    result(I) = "%" & Hex$(CharCode)
                End Select
            End If
        Next I
        URLEncode_ = Join(result, vbNullString)
    End If
End Function

Public Function RemoveHTML_(Text As String) As String
    Dim regexObject As Object
    Set regexObject = CreateObject("vbscript.regexp")
    Dim sOut As String
    Text = Text & vbNullString
    With regexObject
        .Pattern = "<!*[^<>]*>"                  'html tags and comments
        .Global = True
        .IgnoreCase = True
        .MultiLine = True
    End With

    sOut = regexObject.Replace(Text, vbNullString)
    RemoveHTML_ = sOut
End Function

Public Function removeQuotes(Text As String) As String
    removeQuotes = Replace(Text, "&quot;", vbNullString)
End Function


Public Function HtmlDecode(str As String) As String
    If dom Is Nothing Then
     Set dom = CreateObject("htmlfile")
    End If
    
    dom.Open
    dom.Write str
    dom.Close
    HtmlDecode = Trim$(dom.Body.innerText)
    '    HtmlDecode = str
End Function

Public Function Uni2Utf(Text As String) As String
    Dim v As Long
    Dim I As Long
  
    For I = 1 To Len(Text)
        v = AscW(Mid$(Text, I, 1))
        Select Case v
        Case Is < 128
            Uni2Utf = Uni2Utf & Mid$(Text, I, 1)
        Case Is < 2048
            Uni2Utf = Uni2Utf & Chr$(((v And 1984) / 64) Or 192)
            Uni2Utf = Uni2Utf & Chr$((v And 63) Or 128)
        Case Else
            Uni2Utf = Uni2Utf & Chr$(((v And 61440) / 4096) Or 224)
            Uni2Utf = Uni2Utf & Chr$(((v And 4032) / 64) Or 128)
            Uni2Utf = Uni2Utf & Chr$((v And 63) Or 128)
        End Select
    Next
End Function

Public Function Utf2Uni(Text As String) As String
    Dim v As Long
    Dim I As Long: I = 1
    Dim code As Long
  
    Do While I <= Len(Text)
        v = asc(Mid$(Text, I, 1))
        Select Case v
        Case Is < 128
            Utf2Uni = Utf2Uni & Mid$(Text, I, 1)
            I = I + 1
        Case Is < 224
            Utf2Uni = Utf2Uni & ChrW$((v And 63) * 64 + (asc(Mid$(Text, I + 1, 1)) And 63))
            I = I + 2
        Case Else
            code = ((v And 31) * 4096) + _
                                       ((asc(Mid$(Text, I + 1, 1)) And 63) * 64) + _
                                       (asc(Mid$(Text, I + 2, 1)) And 63)
            If code = 12287 Then
                Utf2Uni = Utf2Uni & "'"
            Else
                Utf2Uni = Utf2Uni & ChrW$(code)
            End If
            I = I + 3
        End Select
       
    Loop
End Function

Public Function Base64_HMACSHA256(ByVal sTextToHash As String, ByVal sSharedSecretKey As String) As Variant
    Dim asc As Object
    Dim enc As Object
    Dim TextToHash() As Byte
    Dim SharedSecretKey() As Byte
    Set asc = CreateObject("System.Text.UTF8Encoding")
    Set enc = CreateObject("System.Security.Cryptography.HMACSHA256")

    TextToHash = asc.Getbytes_4(sTextToHash)
    SharedSecretKey = asc.Getbytes_4(sSharedSecretKey)
    enc.key = SharedSecretKey

    Dim bytes() As Byte
    bytes = enc.ComputeHash_2((TextToHash))
    Base64_HMACSHA256 = EncodeBase64(bytes)
    Set asc = Nothing
    Set enc = Nothing
End Function

Public Function Base64_HMACSHA1(ByVal sTextToHash As String, ByVal sSharedSecretKey As String) As Variant
    Dim asc As Object
    Dim enc As Object
    Dim TextToHash() As Byte
    Dim SharedSecretKey() As Byte
    Set asc = CreateObject("System.Text.UTF8Encoding")
    Set enc = CreateObject("System.Security.Cryptography.HMACSHA1")

    TextToHash = asc.Getbytes_4(sTextToHash)
    SharedSecretKey = asc.Getbytes_4(sSharedSecretKey)
    enc.key = SharedSecretKey

    Dim bytes() As Byte
    bytes = enc.ComputeHash_2((TextToHash))
    Base64_HMACSHA1 = EncodeBase64(bytes)
    Set asc = Nothing
    Set enc = Nothing

End Function

Private Function EncodeBase64(ByRef arrData() As Byte) As String

    'Inside the VBE, Go to Tools -> References, then Select Microsoft XML, v6.0
    '(or whatever your latest is. This will give you access to the XML Object Library.)

    Dim objXML As MSXML2.DOMDocument
    Dim objNode As MSXML2.IXMLDOMElement

    Set objXML = New MSXML2.DOMDocument

    ' byte array to base64
    Set objNode = objXML.createElement("b64")
    objNode.DataType = "bin.base64"
    objNode.nodeTypedValue = arrData
    EncodeBase64 = objNode.Text

    Set objNode = Nothing
    Set objXML = Nothing

End Function

Public Function MD5Hex(textString As String) As String
    Dim enc As Variant
    Dim textBytes() As Byte
    Dim bytes As Variant
    Dim outstr As String
    Dim pos As Integer
  
    Set enc = CreateObject("System.Security.Cryptography.MD5CryptoServiceProvider")
    textBytes = textString
    bytes = enc.ComputeHash_2((textBytes))
    
    For pos = 1 To LenB(bytes)
        outstr = outstr & LCase$(Right$("0" & Hex$(AscB(MidB$(bytes, pos, 1))), 2))
    Next
    MD5Hex = outstr
    Set enc = Nothing
  
End Function

Public Function chooseFile(Optional sFileType As String = "*", Optional sTitle As String = vbNullString) As String

    Dim iFileSelect As FileDialog
    Dim sFileName As String
 
    
    Set iFileSelect = Application.FileDialog(msoFileDialogOpen)
    With iFileSelect
        .AllowMultiSelect = False
        .Title = sTitle
        .Filters.Clear
        .Filters.Add "Extensible Markup Language Files", "*." & sFileType
        .InitialView = msoFileDialogViewDetails
        If .Show = -1 Then
            Dim vrtSelectedItem As Variant
            For Each vrtSelectedItem In iFileSelect.SelectedItems
                sFileName = vrtSelectedItem
            Next
        End If
    End With
    Set iFileSelect = Nothing


    chooseFile = sFileName
End Function

Public Sub ChangeQueryParameterValue(ByRef wb As Workbook, ParameterName As String, ParameterValue As String)

    Dim qry As WorkbookQuery
    Dim formula As Variant
    
    '=== Get the query
    Set qry = wb.Queries(ParameterName)
    
    '=== Split the formula into 3 parts and update the second one
    formula = Split(qry.formula, Chr$(34), 3)
    formula(1) = ParameterValue
    
    '=== Update the parameter value
    qry.formula = Join(formula, Chr$(34))
    
End Sub

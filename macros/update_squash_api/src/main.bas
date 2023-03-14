Attribute VB_Name = "main"
Option Explicit
Dim aErrors As Collection


Public Sub main()
    Dim iCol, iRow As Integer
    Dim cCols As Collection
    Dim cRequests As New Collection
    Dim sCol As Variant
    Dim oRequest As cRequest
    Dim sVal As String
    Dim sCufName As String
    Dim arrayVal As Variant
    
    Set jsonConverter = New cJsonConverter
    Set aErrors = New Collection
    
    ConsoleClear
      
    ' lecture des entêtes de colonnes
    iCol = 1
    Set cCols = New Collection
    Worksheets("input_update_REQUIREMENT").Activate
    Do While Cells(1, iCol) <> ""
        cCols.Add key:=Cells(1, iCol).value, Item:=Cells(1, iCol).value
        iCol = iCol + 1
    Loop
    
    ' lecture des données
    iRow = 2
     Do While Cells(iRow, 1) <> ""

        
        iCol = 1
        Set oRequest = New cRequest
        For Each sCol In cCols
            sVal = Cells(iRow, iCol)
            Select Case sCol
            Case "REQ_ID":
                oRequest.setReqId Int(sVal)
            Case "REQ_VERSION_REFERENCE":
                oRequest.setReqRef sVal
            Case "ACTION":
                oRequest.setRequest sVal
            Case "REQ_VERSION_STATUS":
                If (sVal <> "") Then
                    oRequest.setCurrentVersionAttribute "status", sVal
                End If
            Case "REQ_VERSION_CATEGORY":
                oRequest.setCurrentVersionAttribute "category", jsonConverter.ParseJson("{'code':'" & sVal & "' }")
            Case "REQ_VERSION_CRITICALITY":
                oRequest.setCurrentVersionAttribute "criticality", sVal
                
            Case "REQ_VERSION_DESCRIPTION":
                oRequest.setCurrentVersionAttribute "description", sVal
            Case Else
                If Left(sCol, 15) = "REQ_VERSION_CUF" Then
                    sCufName = Right(sCol, Len(sCol) - 16)
                    Select Case sCufName
                        Case "PROFIL", "SECTION", "BLOC", "FONCTION", "VERSION_IMPORT"
                            If sVal <> "" Then
                                arrayVal = Split(sVal, "|")
                                oRequest.setCufValue key:=sCufName, value:=arrayVal
                            Else
                                oRequest.setCufValue key:=sCufName, value:=Array("")
                            End If
                        Case Else
                            oRequest.setCufValue key:=sCufName, value:=sVal
                        End Select
                End If
            End Select

            
            iCol = iCol + 1
        Next

            oRequest.runRequest
 
        
        iRow = iRow + 1
        'If iRow Mod 100 = 0 Then
       '     displayErrors
       ' End If
        
     Loop
     displayErrors
End Sub

Public Sub global_error_handling()
     MsgBox "handle error " & Err.Number & " - " & Err.Description
     displayErrors
End Sub

Public Sub logError(ByVal id As Long, ByVal sMsg As String)
    Debug.Print id & " : " & sMsg
     If Not HasKey(aErrors, "" & id) Then
        aErrors.Add key:="" & id, Item:=Array("" & id, sMsg)
    End If
End Sub

Private Function displayErrors()
    Dim a As Variant
    
    Worksheets("errors").Activate
    Range("A1:C999").Clear
    Range("A1").Select
    For Each a In aErrors
        ActiveCell.value = a(0)
        ActiveCell.Offset(0, 1).Select
        ActiveCell.value = a(1)
        ActiveCell.Offset(1, -1).Select
    Next
    
End Function

#include<stdio.h>
#include<string.h>
#include<sys/socket.h>
#include<arpa/inet.h>
#include<fcntl.h>
#include<unistd.h>
 
int main(int argc , char *argv[])
{
	//Socket Deskriptor
    int sock;
    struct sockaddr_in server;
    char message[1000] , server_reply[2000];
     
    //Erzeuge Socket
	//int socket(int domain, int sockettype, int protocol);
	//AF_INET = Internet IP-Protokoll Version 4 (IPv4)
	//SOCK_STREAM = TCP | SOCK_DGRAM  UDP
	//0 = Standard Protokoll
    sock = socket(AF_INET , SOCK_STREAM , 0);
    if (sock == -1)
    {
        printf("Konnte den Socket nicht erstellen");
    }
    puts("Der Socket wurde erfolgreich erstellt");
     
	//IP Adresse des Remote Servers festlegen
    server.sin_addr.s_addr = inet_addr("127.0.0.1");
	//Adressfamilie festlegen
    server.sin_family = AF_INET;
	//Port des Remote Servers festlgegen
    server.sin_port = htons( 9090 );
 
    //Verbinde zum Server
    if (connect(sock , (struct sockaddr *)&server , sizeof(server)) < 0)
    {
        perror("Verbindung konnte nicht aufgebaut werden. Läuft der Server?");
        return 1;
    }
    puts("Verbindung erfolgreich aufgebaut\n");
     
    //Der Client erwartet eine Nachricht vom Server
	//ssize_t recv ( int socketfd, void *data , size_t data_len, unsigned int flags );
	//flags = Verhalten von revc beeinflussen (wird nicht benötigt)
	if( recv(sock , server_reply , 2000 , 0) < 0)
    {
		puts("Konnte Nachricht vom Server nicht empfangen. Client neu starten");
    }
	puts("Lawinengefahr:\n");
	puts(server_reply);
	//Socket schließen und Programm beenden  
    close(sock);
    return 0;
}

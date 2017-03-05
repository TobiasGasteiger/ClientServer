#include<stdio.h>
#include<string.h>
#include<sys/socket.h>
#include<arpa/inet.h>
#include<unistd.h>
#include<stdlib.h>
#include<time.h>
#include<fcntl.h>

void calculate(char *message); 

int main(int argc , char *argv[])
{
    int socket_desc , client_sock , c , read_size;
    struct sockaddr_in server , client;
    char client_message[2000];
     
    //Create socket
	//int socket(int domain, int sockettype, int protocol);
	//AF_INET = Internet IP-Protokoll Version 4 (IPv4)
	//SOCK_STREAM = TCP | SOCK_DGRAM  UDP
	//0 = Standard Protokoll
    socket_desc = socket(AF_INET , SOCK_STREAM , 0);
    if (socket_desc == -1)
    {
        printf("Konnte den Socket nicht erstellen");
    }
    puts("Der Socket wurde erfolgreich erstellt");
     
    //sockaddr_in Struktur bearbeiten
	//IPv4-Adresse
    server.sin_family = AF_INET;
	//Jede IP-Adresse ist gültig
    server.sin_addr.s_addr = INADDR_ANY;
	//Portnummer der Servers
    server.sin_port = htons( 9090 );
     
    //Den Server and den Port binden
    if( bind(socket_desc,(struct sockaddr *)&server , sizeof(server)) < 0)
    {
        perror("Der Server konnte nicht and die Adresse oder and den Port gebindet werden, läuft der Server bereits?");
        return 1;
    }
    printf("Der Server wurde auf den Port 9090 gestartet\n");

	while(1 == 1){
		//Auf eingehende Verbindungen warte, die Warteschlange ist 3 Plätze groß
		listen(socket_desc , 3);
		puts("Warte auf Client...");
		c = sizeof(struct sockaddr_in);
		//Client Verbindung annehmen
    	client_sock = accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&c);
    	if (client_sock < 0)
    	{
       		perror("Verbindung fehlgschlagen");
       		return 1;
   	 	}
    	puts("Verbindung erfolgreich aufgebaut");

	//Die Nachricht an den Client senden
	//Zufallswerte müssten noch zugewisen werden
	char message[] = "Klausen (523 m): 4\nLajen (1100 m): 1\nLüsen (971 m): 1\nMeransen (1414 m): 5\nMühlbach (777 m): 7\nNatz-Schabs (891 m): 0\nRatschings (976 m): 10\n";
        send(client_sock , message , strlen(message), 0);
	}
	return 0;
}
